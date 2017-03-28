package com.pharbers.aqll.alcalc.alfinaldataprocess

import java.text.SimpleDateFormat

import com.mongodb.casbah.Imports.{DBObject, MongoCursor}
import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.util.dao.{_data_connection_cores, from}
import java.util.{Calendar, UUID}
import java.io.File
import com.pharbers.aqll.util.GetProperties._
import com.pharbers.aqll.alcalc.alfinaldataprocess.csv.scala.CSVWriter
/**
  * Created by liwei on 2017/3/25.
  */

object alFileExport {
  def alFileExport(datatype: String,market : List[String],staend : List[String],company : String,filetype : String) : alExport = {
    try{
      val fmomat_f = new SimpleDateFormat("MM/yyyy")
      var conditions = MongoDBObject()
      market.size match {
        case 0 => conditions = MongoDBObject("Date" -> MongoDBObject("$gte" -> fmomat_f.parse(staend.head).getTime,"$lt" -> fmomat_f.parse(staend.tail.head).getTime))
        case _ => conditions = MongoDBObject("Market" -> MongoDBObject("$in" -> market),"Date" -> MongoDBObject("$gte" -> fmomat_f.parse(staend.head).getTime,"$lt" -> fmomat_f.parse(staend.tail.head).getTime))
      }

      var lst = (from db() in company where conditions).selectOneByOne("hosp_Index")(x => x)(_data_connection_cores)
      datatype match {
        case "省份数据" => lst = (from db() in company where conditions).selectOneByOne("prov_Index")(x => x)(_data_connection_cores)
        case "城市数据" => lst = (from db() in company where conditions).selectOneByOne("city_Index")(x => x)(_data_connection_cores)
        case "医院数据" => lst = lst
      }

      lst.size match {
        case 0 => returnMess(-1,"no matching data.","Unknown")
        case _ => {
          val file : File = createFile(filetype)
          val writer = CSVWriter.open(file,"GBK")
          writeFileHead(datatype,writer)
          weightSum(lst, datatype, writer)
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
  def weightSum(lst: MongoCursor,datatype: String, writer : CSVWriter): Unit ={
    var b : Option[DBObject] = None
    var f_units_sum,f_sales_sum = 0.0
    var num = 0
    val total = lst.size
    while(lst.hasNext) {
      val c : DBObject = lst.next()
      num = num + 1
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
    val date = timeDate.get(Calendar.YEAR)+""+(timeDate.get(Calendar.MONTH))+1
    datatype match {
      case "省份数据" => {
        writer.writeRow(date :: x.get("Provice") :: x.get("Market") :: x.get("Product") :: f_units_sum :: f_sales_sum :: Nil)
      }
      case "城市数据" =>{
        writer.writeRow(date :: x.get("Provice") :: x.get("City") :: x.get("Market") :: x.get("Product") :: f_units_sum :: f_sales_sum :: Nil)
      }
      case "医院数据" => {
        writer.writeRow(date :: x.get("Provice") :: x.get("City") :: x.get("Panel_ID") :: x.get("Market") :: x.get("Product") :: f_units_sum :: f_sales_sum :: Nil)
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
}