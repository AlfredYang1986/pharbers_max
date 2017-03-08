package module.business

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.pharbers.aqll.pattern.ModuleTrait
import com.pharbers.aqll.pattern.MessageDefines
import com.pharbers.aqll.pattern.CommonMessage
import java.io.File

import com.pharbers.aqll.util.dao.from
import com.mongodb.casbah.Imports.{$and, DBObject, _}
import com.pharbers.aqll.util.dao._data_connection_cores
import java.text.SimpleDateFormat
import java.util.{Calendar, UUID}

import com.mongodb.DBObject
import com.pharbers.aqll.util.GetProperties

import scala.collection.immutable.List
import com.pharbers.aqll.util.file.csv.scala._

import scala.collection.mutable.ListBuffer
import com.pharbers.aqll.util.Superglobals._

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

        def dateListConditions(getter : JsValue => Any)(key : String, value : JsValue) : Option[DBObject] = getter(value) match {
            case None => None
            case Some(x) => {
                val fm = new SimpleDateFormat("MM/yyyy")
                val start = fm.parse(x.asInstanceOf[List[String]].head).getTime
                val end = fm.parse(x.asInstanceOf[List[String]].last).getTime
                Some("Date" $gte start $lte end)
            }
        }

        def marketListConditions(getter : JsValue => Any)(key : String, value : JsValue) : Option[DBObject] = getter(value) match {
            case None => None
            case Some(x) => {
                val lst = x.asInstanceOf[List[String]].map { str => str }
                Some("Market" $in lst)
            }
        }

        def conditionsAcc(o : List[DBObject], keys : List[String], func : (String, JsValue) => Option[DBObject]) : List[DBObject] = keys match {
            case Nil => o
            case head :: lst => func(head, (data \ head).as[JsValue]) match {
                case None => conditionsAcc(o, lst, func)
                case Some(y) => conditionsAcc(y :: o, lst, func)
            }
        }

        def conditions : List[DBObject] = {
            var con = conditionsAcc(Nil, "Date" :: Nil, dateListConditions(x => x.asOpt[List[String]]))
            con = conditionsAcc(con, "market" :: Nil, marketListConditions(x => x.asOpt[List[String]]))
            con
        }
        try {
            (Some(Map("finalResult" -> toJson(write_CsvFile(data,conditions)))), None)
        } catch {
            case ex : Exception => (None, Some(error_handler(ex.getMessage().toInt)))
        }
    }

    def write_CsvFile(data : JsValue,conditions : List[DBObject]) : String = {
        val connectionName = (data \ "company").asOpt[String].get
        val fileName = UUID.randomUUID + SUFFIX_CSV
        val file : File = new File(GetProperties.Client_Export_FilePath)
        if(!file.exists()) file.mkdir()
        val file1 : File = new File(GetProperties.Client_Export_FilePath+fileName)
        val writer = CSVWriter.open(file1,"GBK")
        writer.writeRow(List("Panel_ID","Date","City","Product","Sales","Units"))
        var first = ZERO
        var step = TEN_THOUSAND
        val sum = (from db() in connectionName where $and(conditions)).count(_data_connection_cores)
        while (first < sum) {
            val result = (from db() in connectionName where $and(conditions)).selectSkipTop(first)(step)("Date")(finalResultTempJsValue(_))(_data_connection_cores).toList
            writeConFunc(result,writer)
            if(sum - first < step){step = sum - first}
            first += step
            println(first)
        }
        writer.close()
        fileName
    }

    def writeConFunc(result : List[Map[String,JsValue]],writer : CSVWriter) {
        result.foreach { x =>
            val lb : ListBuffer[AnyRef] = ListBuffer[AnyRef]()
            lb.append(x.get("Panel_ID").get)
            lb.append(x.get("Date").get)
            lb.append(x.get("City").get)
            lb.append(x.get("Product").get)
            lb.append(x.get("Sales").get)
            lb.append(x.get("Units").get)
            writer.writeRow(lb.toList)
        }
    }

    def finalResultTempJsValue(x : MongoDBObject) : Map[String,JsValue] = {
        val timeDate = Calendar.getInstance
        timeDate.setTimeInMillis(x.getAs[Number]("Date").get.longValue())
        var year = timeDate.get(Calendar.YEAR).toString
        var month = (timeDate.get(Calendar.MONTH)+1).toString
        Map(
            "Date" -> toJson(year + (if(month.length<2){"0"+month}else{month})),
            "Provice" -> toJson(x.getAs[String]("Provice").get),
            "City" -> toJson(x.getAs[String]("City").get),
            "Panel_ID" -> toJson(x.getAs[String]("Panel_ID").get),
            "Market" -> toJson(x.getAs[String]("Market").get),
            "Product" -> toJson(x.getAs[String]("Product").get),
            "Sales" -> toJson(f"${x.getAs[Number]("f_sales").get.doubleValue}%1.2f"),
            "Units" -> toJson(f"${x.getAs[Number]("f_units").get.doubleValue}%1.2f")
        )
    }
}
