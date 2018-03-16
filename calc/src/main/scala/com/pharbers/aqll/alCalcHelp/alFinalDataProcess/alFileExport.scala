package com.pharbers.aqll.alCalcHelp.alFinalDataProcess

import java.io.File
import java.util.UUID
import com.mongodb.casbah.Imports._
import scala.collection.immutable.Map
import com.pharbers.aqll.common.alDao.from
import com.pharbers.aqll.alCalcHelp.dbcores._
import com.pharbers.driver.redis.phRedisDriver
import com.pharbers.aqll.common.alString.alStringOpt._
import com.pharbers.aqll.common.alDate.scala.alDateOpt._
import com.pharbers.aqll.alStart.alHttpFunc.alExportItem
import com.pharbers.aqll.common.alFileHandler.fileConfig._
import com.pharbers.aqll.alCalcHelp.alWebSocket.phWebSocket
import com.pharbers.aqll.common.alFileHandler.alCsvOpt.scala.CSVWriter

/**
  * Created by liwei on 2017/3/25.
  *     Modify by clock on 2017.12.26s
  */
case class alFileExport(item: alExportItem) {
    val Provice = "provinces"
    val City = "city"
    val Hospital = "hospital"

    var f_units_sum, f_sales_sum, f_units_sum2, f_sales_sum2 = 0.0
    var dbo: Option[DBObject] = None
    var num = 0
    var filename = ""
    var total = 0
    val company = phRedisDriver().commonDriver.hget(item.uid, "company").map(x=>x).getOrElse(throw new Exception("redis not found company"))

    def export: String = {
        try {
            val mktCondition = item.market.map(x => removeSpace(x)).filter(!_.isEmpty) match {
                case Nil => None
                case head :: _ => Some("Market" -> head)
            }

            val dateCondition = item.staend.map(x => removeSpace(x)).filter(!_.isEmpty) match {
                case Nil => None
                case start :: end :: Nil => Some("Date" -> MongoDBObject(
                        "$gte" -> yyyyMM2Long(start),
                        "$lt" -> yyyyMM2Long(end)
                    ))
                case _ => throw new Exception("staend args error")
            }

            val conditions = MongoDBObject.newBuilder
            (mktCondition :: dateCondition :: Nil).foreach {
                case Some(con) => conditions += con
                case _ => Unit
            }

            val lst = item.datatype match {
                case Provice => (from db() in company where conditions.result).selectOneByOne("prov_Index")(x => x)
                case City => (from db() in company where conditions.result).selectOneByOne("city_Index")(x => x)
                case Hospital => (from db() in company where conditions.result).selectOneByOne("hosp_Index")(x => x)
            }

            total = lst.size
            total match {
                case 0 => throw new Exception("warn data does not exist")
                case _ =>
                    val file: File = createFile(item.filetype)
                    val writer = CSVWriter.open(file, "GBK")
                    writer.writeRow(writeFileHead(item.datatype))
                    while (lst.hasNext) {
                        val c: DBObject = lst.next()
                        num = num + 1
                        calculateProgress(num, total)
                        dbo match {
                            case None =>
                                dbo = Some(c)
                                overrideSum(c, added = false)
                                isInsertData(total == num, c, writer)

                            case Some(x) => matchPCHData(x, c, writer)
                        }
                    }
                    writer.close()
                    filename = file.getName
            }

            filename
        } catch {
            case e: Exception => e.getMessage
        }
    }

    private def createFile(filetype: String): File = {
        val file_f: File = new File(memorySplitFile + export_file)
        if (!file_f.exists())
            file_f.mkdir()
        new File(memorySplitFile + export_file + UUID.randomUUID + filetype)
    }

    private def matchPCHData(x: DBObject, c: DBObject, writer: CSVWriter): Unit = {
        if (matchPCHIndex(x, c)) {
            overrideSum(c, added = true)
            isInsertData(total == num && f_units_sum != 0.0 && f_sales_sum != 0.0, c, writer)
        } else {
            isInsertData(f_units_sum != 0.0 && f_sales_sum != 0.0, c, writer)
            f_units_sum2 = f_units_sum2 + f_units_sum
            f_sales_sum2 = f_sales_sum2 + f_sales_sum
            dbo = Some(c)
            overrideSum(c, added = false)
        }
    }

    private def matchPCHIndex(x: DBObject, c: DBObject): Boolean = item.datatype match {
        case Provice => x.get("prov_Index").equals(c.get("prov_Index"))
        case City => x.get("city_Index").equals(c.get("city_Index"))
        case Hospital => x.get("hosp_Index").equals(c.get("hosp_Index"))
        case _ => false
    }

    private def overrideSum(c: DBObject, added: Boolean): Unit = {
        if (added) {
            f_units_sum = f_units_sum + c.get("f_units").asInstanceOf[Double]
            f_sales_sum = f_sales_sum + c.get("f_sales").asInstanceOf[Double]
        } else {
            f_units_sum = c.get("f_units").asInstanceOf[Double]
            f_sales_sum = c.get("f_sales").asInstanceOf[Double]
        }
    }

    private def isInsertData(isinsert: Boolean, c: DBObject, writer: CSVWriter) = {
        if (isinsert)
            writeFileBody(writer, c, item.datatype, f_units_sum, f_sales_sum)
        else
            Unit
    }

    private def calculateProgress(num: Int, total: Int) {
        var n = total
        if (n % 100 != 0)
            n = n - (n % 100)
        if ((num % (n / 100)) == 0) {
            val msg = Map(
                "type" -> "progress",
                "progress" -> "1"
            )
            phWebSocket(item.uid).post(msg)
        }
    }

    private def writeFileHead(datatype: String): List[String] = datatype match {
        case Provice => (xml.XML.loadFile(export_xml) \ "body" \ "Provice").map(x => x.text).toList
        case City => (xml.XML.loadFile(export_xml) \ "body" \ "City").map(x => x.text).toList
        case Hospital => (xml.XML.loadFile(export_xml) \ "body" \ "Hospital").map(x => x.text).toList
    }

    private def writeFileBody(writer: CSVWriter, x: DBObject, datatype: String, f_units_sum: Double, f_sales_sum: Double) {
        val date = Timestamp2yyyyMM(x.get("Date").asInstanceOf[Number].longValue())
        datatype match {
            case Provice => writer.writeRow(date :: x.get("Provice") :: x.get("Market") :: x.get("Product") :: f_sales_sum :: f_units_sum :: Nil)
            case City => writer.writeRow(date :: x.get("Provice") :: x.get("City") :: x.get("Market") :: x.get("Product") :: f_sales_sum :: f_units_sum :: Nil)
            case Hospital => writer.writeRow(date :: x.get("Provice") :: x.get("City") :: x.get("Panel_ID") :: x.get("Market") :: x.get("Product") :: f_sales_sum :: f_units_sum :: Nil)
        }
    }
}