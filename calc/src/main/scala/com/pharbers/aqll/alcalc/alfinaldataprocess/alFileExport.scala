package com.pharbers.aqll.alcalc.alfinaldataprocess

import java.text.SimpleDateFormat

import com.mongodb.casbah.Imports.{DBObject, MongoCursor}
import com.mongodb.casbah.commons.MongoDBObject
import java.util.{Calendar, UUID}
import java.io.File

import com.pharbers.aqll.alcalc.alemchat.sendMessage
import com.pharbers.aqll.alcalc.alfinaldataprocess.csv.scala.CSVWriter
import com.pharbers.aqll.common.alDao.{_data_connection_cores, from}
import com.pharbers.aqll.old.calc.util.StringOption

import com.pharbers.aqll.alcalc.alCommon.fileConfig._

/**
  * Created by liwei on 2017/3/25.
  */

case class alFilesExport(datatype: String,
                        market : List[String],
                        staend : List[String],
                        company : String,
                        filetype : String,
                        uname: String)

object alFileExport {
  def alFileExport(alExport: alFilesExport) : alExport = {
    try{
      val fmomat_f = new SimpleDateFormat("MM/yyyy")
      var conditions = MongoDBObject()
      val markets = alExport.market.map(x => StringOption.takeStringSpace(x))
      markets.size match {
        case 0 => conditions = MongoDBObject("Date" -> MongoDBObject("$gte" -> fmomat_f.parse(alExport.staend.head).getTime,"$lt" -> fmomat_f.parse(alExport.staend.tail.head).getTime))
        case _ => conditions = MongoDBObject("Market" -> MongoDBObject("$in" -> markets),"Date" -> MongoDBObject("$gte" -> fmomat_f.parse(alExport.staend.head).getTime,"$lt" -> fmomat_f.parse(alExport.staend.tail.head).getTime))
      }

      var lst = (from db() in alExport.company where conditions).selectOneByOne("hosp_Index")(x => x)(_data_connection_cores)
      alExport.datatype match {
        case "省份数据" => lst = (from db() in alExport.company where conditions).selectOneByOne("prov_Index")(x => x)(_data_connection_cores)
        case "城市数据" => lst = (from db() in alExport.company where conditions).selectOneByOne("city_Index")(x => x)(_data_connection_cores)
        case "医院数据" => lst = lst
      }

      lst.size match {
        case 0 => returnMess(-1,"no matching data.","Unknown")
        case _ => {
          val file : File = createFile(alExport.filetype)
          val writer = CSVWriter.open(file,"GBK")
          writeFileHead(alExport.datatype, writer)
          weightSum(lst, alExport.datatype, writer, alExport.uname)
          writer.close()
          returnMess(0,"file generation success.",file.getName)
        }
      }
    } catch {
      case e:Exception => {
        println(e.printStackTrace())
      }
      returnMess(-1,"system internal error,file generation failed.","Unknown")
    }
  }
  // TODO :  创建文件夹
  def createFile(filetype: String): File ={
    val file_f : File = new File(fileBase + export_file)
    if(!file_f.exists()) file_f.mkdir()
    val file_t : File = new File(fileBase + export_file + UUID.randomUUID + filetype)
    file_t
  }
  // TODO :  写入文件头部部分
  def writeFileHead(datatype: String,writer: CSVWriter) {
    val someXml : String = export_xml
    var fields: List[String] = Nil
    datatype match {
      case "省份数据" => {
        fields = ((xml.XML.loadFile(someXml) \ "body" \ "Provice").map(x => x.text)).toList
      }
      case "城市数据" => {
        fields = ((xml.XML.loadFile(someXml) \ "body" \ "City").map(x => x.text)).toList
      }
      case "医院数据" => {
        fields = ((xml.XML.loadFile(someXml) \ "body" \ "Hospital").map(x => x.text)).toList
      }
    }
    writer.writeRow(fields)
  }
  // TODO :  根据datatype求权和
  def weightSum(lst: MongoCursor,datatype: String, writer : CSVWriter, uname: String): Unit ={
    var b : Option[DBObject] = None
    var f_units_sum,f_sales_sum = 0.0
    var num = 0
    val total = lst.size
    while(lst.hasNext) {
      val c : DBObject = lst.next()
      num = num + 1
      stepVal(num, total, uname)
      b match {
        case None => {
          b = Some(c)
          f_units_sum = c.get("f_units").asInstanceOf[Double]
          f_sales_sum = c.get("f_sales").asInstanceOf[Double]
          if(total == num) writeFileBody(writer,c,datatype,f_units_sum,f_sales_sum)
        }
        case Some(x) => {
          var flag = false
          datatype match {
            case "省份数据" => flag = x.get("prov_Index").equals(c.get("prov_Index"))
            case "城市数据" => flag = x.get("city_Index").equals(c.get("city_Index"))
            case "医院数据" => flag = x.get("hosp_Index").equals(c.get("hosp_Index"))
          }
          flag match {
            case true => {
              f_units_sum = f_units_sum + c.get("f_units").asInstanceOf[Double]
              f_sales_sum = f_sales_sum + c.get("f_sales").asInstanceOf[Double]
              if(total == num) writeFileBody(writer,x,datatype,f_units_sum,f_sales_sum)
            }
            case false => {
              writeFileBody(writer,x,datatype,f_units_sum,f_sales_sum)
              b = Some(c)
              f_units_sum = c.get("f_units").asInstanceOf[Double]
              f_sales_sum = c.get("f_sales").asInstanceOf[Double]
            }
          }
        }
      }
    }
  }
  // TODO :  写入文件主体部分
  def writeFileBody(writer: CSVWriter,x:DBObject,datatype:String,f_units_sum:Double,f_sales_sum:Double){
    val timeDate = Calendar.getInstance
    timeDate.setTimeInMillis(x.get("Date").asInstanceOf[Number].longValue())
    val m = timeDate.get(Calendar.MONTH)+1
    var mm = ""
    if(m.toString.length < 2) mm = "0"+m else mm = m.toString
    val date = timeDate.get(Calendar.YEAR)+""+mm
    datatype match {
      case "省份数据" => {
        writer.writeRow(date :: x.get("Provice") :: x.get("Market") :: x.get("Product") :: f_sales_sum :: f_units_sum :: Nil)
      }
      case "城市数据" =>{
        writer.writeRow(date :: x.get("Provice") :: x.get("City") :: x.get("Market") :: x.get("Product") :: f_sales_sum :: f_units_sum :: Nil)
      }
      case "医院数据" => {
        writer.writeRow(date :: x.get("Provice") :: x.get("City") :: x.get("Panel_ID") :: x.get("Market") :: x.get("Product") :: f_sales_sum :: f_units_sum :: Nil)
      }
    }
  }
  // TODO :  返回
  def returnMess(status: Integer,message: String,filename: String): alExport ={
    val alexport : alExport = new alExport()
    alexport.setStatus(status)
    alexport.setMessage(message)
    alexport.setFilename(filename)
    alexport
  }

  def stepVal(num: Int,total: Int, uname: String) {
    var n = total
    if(n%100!=0){
      n=n-(n%100)
    }
    if((num % (n/100))==0){
      sendMessage.sendMsg("1", uname, Map("uuid" -> "", "company" -> "", "type" -> "progress"))
    }
  }
}