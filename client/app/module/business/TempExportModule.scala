package module.business

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.pharbers.aqll.pattern.ModuleTrait
import com.pharbers.aqll.pattern.MessageDefines
import com.pharbers.aqll.pattern.CommonMessage
import com.pharbers.aqll.util.dao.{_data_connection_cores, from}
import java.text.SimpleDateFormat
import java.util.{Calendar, UUID}
import java.io.File

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.BasicDBObject
import com.mongodb.DBObject
import com.pharbers.aqll.util.GetProperties
import com.pharbers.aqll.util.file.csv.scala._

import scala.collection.mutable.ListBuffer
import com.pharbers.aqll.util.DateUtil._

import scala.collection.immutable.List
/**
  * Created by Wli on 2017/2/13 0013.
  */

object TempExportModuleMessage {
    sealed class msg_tempexportBase extends CommonMessage
    case class msg_finalresult1(data : JsValue) extends msg_tempexportBase
}

object TempExportModule  extends ModuleTrait{
    import TempExportModuleMessage._
    import controllers.common.default_error_handler.f
    def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]]) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_finalresult1(data) => msg_finalresult_func(data)
        case _ => ???
    }

    def msg_finalresult_func(data : JsValue)(implicit error_handler : Int => JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
        var datatype = data.\("datatype").get.asOpt[String].get
        var datetime = data.\("staend").get.asOpt[Array[String]].get
        var markets = data.\("market").get.asOpt[Array[String]].get
        var company = data.\("company").get.asOpt[String].get

        try {
            var _group : DBObject = new BasicDBObject("Date", "$Date")
            datatype match {
                case "省份数据" => groupField("Provice",_group)
                case "城市数据" => groupField("City",_group)
                case "医院数据" => groupField("Hospital",_group)
            }
            val groupFields : DBObject = new BasicDBObject("_id", _group)
            groupFields.put("Sales", new BasicDBObject("$sum", "$f_sales"))
            groupFields.put("Units", new BasicDBObject("$sum", "$f_units"))
            val group : DBObject = new BasicDBObject("$group", groupFields)
            val fm = new SimpleDateFormat("MM/yyyy")
            val conditions : List[DBObject] = List(("$match" $eq ("Market" $in markets)),("$match" $eq ("Date" $gte fm.parse(datetime.head).getTime $lte fm.parse(datetime.tail.head).getTime)),group)
            println(conditions)
            val result = (from db() in company where conditions).selectAggregate(resultData(_,datatype))(_data_connection_cores).toList
            println(result.size)
            (Some(Map("finalResult" -> toJson(MkCsvFile(datatype,result)))), None)
        } catch {
            case ex : Exception => (None, Some(error_handler(ex.getMessage().toInt)))
        }
    }

    def groupField(datatype : String,_group : DBObject) {
        val someXml : String = "xml/export/FileExport.xml"
        var fields : List[String] = ((xml.XML.loadFile(someXml) \ "group" \ datatype).map (x => x.text)).toList
        fields.foreach(x => _group.put(x, "$"+x))
    }

    def resultData(x: MongoDBObject,datatype: String): Map[String,JsValue] = {
        val _id = x.getAs[MongoDBObject]("_id").get
        val timeDate = Calendar.getInstance
        timeDate.setTimeInMillis(_id.getAs[Number]("Date").get.longValue)
        var yearmonth = s"${timeDate.get(Calendar.YEAR).toString}${polishMonth((timeDate.get(Calendar.MONTH)+1).toString)}"
        var map = Map("Date" -> toJson(yearmonth))
        datatype match {
            case "省份数据" => {
                map ++ Map(
                    "Provice" -> toJson(_id.getAs[String]("Provice").get),
                    "Market" -> toJson(_id.getAs[String]("Market").get),
                    "Product" -> toJson(_id.getAs[String]("Product").get),
                    "Sales" -> toJson(x.getAs[Number]("Sales").get.doubleValue()),
                    "Units" -> toJson(x.getAs[Number]("Units").get.doubleValue())
                )
            }
            case "城市数据" => {
                map ++ Map(
                    "Provice" -> toJson(_id.getAs[String]("Provice").get),
                    "City" -> toJson(_id.getAs[String]("City").get),
                    "Market" -> toJson(_id.getAs[String]("Market").get),
                    "Product" -> toJson(_id.getAs[String]("Product").get),
                    "Sales" -> toJson(x.getAs[Number]("Sales").get.doubleValue()),
                    "Units" -> toJson(x.getAs[Number]("Units").get.doubleValue())
                )
            }
            case "医院数据" => {
                map ++ Map(
                    "Provice" -> toJson(_id.getAs[String]("Provice").get),
                    "City" -> toJson(_id.getAs[String]("City").get),
                    "Panel_ID" -> toJson(_id.getAs[String]("Panel_ID").get),
                    "Market" -> toJson(_id.getAs[String]("Market").get),
                    "Product" -> toJson(_id.getAs[String]("Product").get),
                    "Sales" -> toJson(x.getAs[Number]("Sales").get.doubleValue()),
                    "Units" -> toJson(x.getAs[Number]("Units").get.doubleValue())
                )
            }
        }
    }

    def MkCsvFile(datatype : String, result : List[Map[String,JsValue]]) : String = {
        val fileName = UUID.randomUUID + ".csv"
        val file : File = new File(GetProperties.Client_Export_FilePath)
        if(!file.exists()) file.mkdir()
        val file1 : File = new File(GetProperties.Client_Export_FilePath+fileName)
        val writer = CSVWriter.open(file1,"GBK")
        val someXml : String = "xml/export/FileExport.xml"
        datatype match {
            case "省份数据" => {
                var fields : List[String] = ((xml.XML.loadFile(someXml) \ "body" \ "Provice").map (x => x.text)).toList
                writeCSV(result,fields,writer)
            }
            case "城市数据" => {
                var fields : List[String] = ((xml.XML.loadFile(someXml) \ "body" \ "City").map (x => x.text)).toList
                writeCSV(result,fields,writer)
            }
            case "医院数据" => {
                var fields : List[String] = ((xml.XML.loadFile(someXml) \ "body" \ "Hospital").map (x => x.text)).toList
                writeCSV(result,fields,writer)
            }
        }
        writer.close()
        fileName
    }

    def writeCSV(result: List[Map[String,JsValue]], fields: List[String], writer: CSVWriter) {
        writer.writeRow(fields)
        result.foreach{ x =>
            val lb : ListBuffer[AnyRef] = ListBuffer[AnyRef]()
            fields.foreach( y => lb.append(x.get(y).get))
            writer.writeRow(lb.toList)
        }
    }
}
