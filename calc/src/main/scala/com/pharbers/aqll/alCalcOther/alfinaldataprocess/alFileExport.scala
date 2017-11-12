package com.pharbers.aqll.alCalcOther.alfinaldataprocess

import com.mongodb.casbah.Imports._
import com.pharbers.aqll.alCalcOther.alMessgae.{alWebSocket}
import com.pharbers.aqll.common.alDao.from
import com.pharbers.aqll.common.alDate.scala.alDateOpt._
import com.pharbers.aqll.common.alErrorCode.alErrorCode._
import com.pharbers.aqll.common.alFileHandler.alCsvOpt.scala.CSVWriter
import com.pharbers.aqll.common.alFileHandler.fileConfig._
import com.pharbers.aqll.common.alString.alStringOpt._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.pharbers.aqll.alCalaHelp.dbcores._
import java.io.File
import java.util.UUID

import scala.collection.immutable.Map

/**
  * Created by liwei on 2017/3/25.
  */
case class alExport(datatype: String,
                    market: List[String],
                    staend: List[String],
                    company: String,
                    filetype: String,
                    uname: String)

case class alFileExport() {

    def apply(export: alExport): JsValue = {
        val Provice = "省份数据"
        val City = "城市数据"
        val Hospital = "医院数据"

        try {
            val conditions = export.market.map(x => removeSpace(x)) match {
                case Nil => MongoDBObject("Date" -> MongoDBObject("$gte" -> MMyyyy2Long(export.staend.head), "$lt" -> MMyyyy2Long(export.staend.tail.head)))
                case m => MongoDBObject("Market" -> MongoDBObject("$in" -> m), "Date" -> MongoDBObject("$gte" -> MMyyyy2Long(export.staend.head), "$lt" -> MMyyyy2Long(export.staend.tail.head)))
            }

            val lst = export.datatype match {
                case Provice => (from db() in export.company where conditions).selectOneByOne("prov_Index")(x => x)
                case City => (from db() in export.company where conditions).selectOneByOne("city_Index")(x => x)
                case Hospital => (from db() in export.company where conditions).selectOneByOne("hosp_Index")(x => x)
            }

            var dbo: Option[DBObject] = None
            var f_units_sum, f_sales_sum, f_units_sum2, f_sales_sum2 = 0.0
            val total = lst.size
            var num = 0
            var filename = ""
            total match {
                case 0 => throw new Exception("warn data does not exist")
                case _ => {
                    val file: File = createFile(export.filetype)
                    val writer = CSVWriter.open(file, "GBK")
                    writer.writeRow(writeFileHead(export.datatype))
                    while (lst.hasNext) {
                        val c: DBObject = lst.next()
                        num = num + 1
                        calculateProgress(num, total)
                        dbo match {
                            case None => {
                                dbo = Some(c)
                                overrideSum(c, false)
                                isInsertData((total == num), c, writer)
                            }
                            case Some(x) => matchPCHData(x, c, writer)
                        }
                    }
                    writer.close()
                    filename = file.getName
                }
            }

            /**
              * 创建文件
              *
              * @param filetype
              * @return
              */
            def createFile(filetype: String): File = {
                val file_f: File = new File(root + program + fileBase + export_file)
                if (!file_f.exists()) file_f.mkdir()
                //        new File(root+ program + fileBase + export_file + UUID.randomUUID + filetype)
                new File(fileBase + export_file + UUID.randomUUID + filetype)
            }

            /**
              * 匹配省份城市医院索引
              *
              * @param x
              * @param c
              * @return
              */
            def matchPCHIndex(x: DBObject, c: DBObject): Boolean = export.datatype match {
                case Provice => x.get("prov_Index").equals(c.get("prov_Index"))
                case City => x.get("city_Index").equals(c.get("city_Index"))
                case Hospital => x.get("hosp_Index").equals(c.get("hosp_Index"))
                case _ => false
            }

            /**
              * 匹配省份城市医院数据
              *
              * @param x
              * @param c
              * @param writer
              */
            def matchPCHData(x: DBObject, c: DBObject, writer: CSVWriter): Unit = {
                matchPCHIndex(x, c) match {
                    case true => {
                        overrideSum(c, true)
                        isInsertData((total == num && f_units_sum != 0.0 && f_sales_sum != 0.0), c, writer)
                    }
                    case false => {
                        isInsertData((f_units_sum != 0.0 && f_sales_sum != 0.0), c, writer)
                        f_units_sum2 = f_units_sum2 + f_units_sum
                        f_sales_sum2 = f_sales_sum2 + f_sales_sum
                        dbo = Some(c)
                        overrideSum(c, false)
                    }
                }
            }

            /**
              * 写入数据
              *
              * @param isinsert
              * @param c
              * @param writer
              */
            def isInsertData(isinsert: Boolean, c: DBObject, writer: CSVWriter): Unit = {
                if (isinsert) writeFileBody(writer, c, export.datatype, f_units_sum, f_sales_sum)
            }

            /**
              * 匹配求和
              *
              * @param c
              * @param isadd
              */
            def overrideSum(c: DBObject, isadd: Boolean): Unit = {
                isadd match {
                    case true => {
                        f_units_sum = f_units_sum + c.get("f_units").asInstanceOf[Double]
                        f_sales_sum = f_sales_sum + c.get("f_sales").asInstanceOf[Double]
                    }
                    case false => {
                        f_units_sum = c.get("f_units").asInstanceOf[Double]
                        f_sales_sum = c.get("f_sales").asInstanceOf[Double]
                    }
                }
            }

            /**
              * 写入文件头部部分
              *
              * @param datatype
              * @return
              */
            def writeFileHead(datatype: String): List[String] = datatype match {
                case Provice => ((xml.XML.loadFile(export_xml) \ "body" \ "Provice").map(x => x.text)).toList
                case City => ((xml.XML.loadFile(export_xml) \ "body" \ "City").map(x => x.text)).toList
                case Hospital => ((xml.XML.loadFile(export_xml) \ "body" \ "Hospital").map(x => x.text)).toList
            }

            /**
              * 写入文件主体部分
              *
              * @param writer
              * @param x
              * @param datatype
              * @param f_units_sum
              * @param f_sales_sum
              */
            def writeFileBody(writer: CSVWriter, x: DBObject, datatype: String, f_units_sum: Double, f_sales_sum: Double) {
                val date = Timestamp2yyyyMM(x.get("Date").asInstanceOf[Number].longValue())
                datatype match {
                    case Provice => writer.writeRow(date :: x.get("Provice") :: x.get("Market") :: x.get("Product") :: f_sales_sum :: f_units_sum :: Nil)
                    case City => writer.writeRow(date :: x.get("Provice") :: x.get("City") :: x.get("Market") :: x.get("Product") :: f_sales_sum :: f_units_sum :: Nil)
                    case Hospital => writer.writeRow(date :: x.get("Provice") :: x.get("City") :: x.get("Panel_ID") :: x.get("Market") :: x.get("Product") :: f_sales_sum :: f_units_sum :: Nil)
                }
            }

            /**
              * 计算进度条
              *
              * @param num
              * @param total
              */
            def calculateProgress(num: Int, total: Int) {
                var n = total
                if (n % 100 != 0) n = n - (n % 100)
                if ((num % (n / 100)) == 0) {
                    val msg = Map(
                        "type" -> "progress",
                        "progress" -> "1"
                    )
                    alWebSocket(export.uname).post(msg)
                    //new alMessageProxy().sendMsg("1", export.uname, Map("uuid" -> "", "company" -> "", "type" -> "progress"))
                }
            }

            toJson(successToJson(toJson(filename)).get)
        } catch {
            case e: Exception => errorToJson(e.getMessage)
        }
    }
}