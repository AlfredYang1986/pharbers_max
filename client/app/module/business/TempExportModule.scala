package module.business

import java.io.File

import com.pharbers.aqll.pattern.{CommonMessage, MessageDefines, ModuleTrait}
import com.pharbers.aqll.util.GetProperties
import com.pharbers.aqll.util.dao.{_data_connection_cores, from}
import com.pharbers.aqll.util.file.csv.scala.CSVWriter
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.mongodb.DBObject
import com.mongodb.casbah.Imports.{$and, _}
import java.util.{Calendar, UUID}
import java.util.Date
import scala.collection.immutable.List
import scala.collection.mutable.ListBuffer

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
        try {
            println(new Date())
            val fileName = UUID.randomUUID + ".csv"
            val file : File = new File(GetProperties.Client_Export_FilePath+fileName)
            if(!file.exists()){file.createNewFile()}
            val writer = CSVWriter.open(file,"GBK")
            writer.writeRow(List("Panel_ID","Date","City","Product","f_sales","f_units"))
            val connectionName = (data \ "company").asOpt[String].get
            val order = "Timestamp"
            var first = 0
            var step = 10000
            val sum = (from db() in connectionName).count(_data_connection_cores)
            var temp: List[Map[String,JsValue]] = List.empty
            while (first < sum) {
                val result = (from db() in connectionName).selectSkipTop(first)(step)(order)(finalResultJsValue1(_))(_data_connection_cores).toList
                temp = groupBy4(result)(temp)
                println(s"tempsize =${temp.size}")
                if(sum - first < step){
                    step = sum - first
                }
                first += step
            }
            temp.foreach{ x =>
                val lb : ListBuffer[AnyRef] = ListBuffer[AnyRef]()
                lb.append(x.get("Panel_ID").get)
                lb.append(x.get("Date").get)
                lb.append(x.get("City").get)
                lb.append(x.get("Product").get)
                lb.append(x.get("f_sales").get)
                lb.append(x.get("f_units").get)
                writer.writeRow(lb.toList)
            }
            writer.close()
            println(new Date())
            (Some(Map("finalResult" -> toJson(fileName))), None)
        } catch {
            case ex : Exception => (None, Some(error_handler(ex.getMessage().toInt)))
        }
    }

    def finalResultJsValue1(obj : DBObject) : Map[String,JsValue] = {
        Map(
            "Panel_ID" -> toJson(obj.get("Panel_ID").asInstanceOf[String]),
            "Date" -> toJson(obj.get("Date").asInstanceOf[String]),
            "City" -> toJson(obj.get("City").asInstanceOf[String]),
            "Product" -> toJson(obj.get("Product").asInstanceOf[String]),
            "f_sales" -> toJson(obj.get("f_sales").asInstanceOf[Double]),
            "f_units" -> toJson(obj.get("f_units").asInstanceOf[Double])
        )
	}

    def groupBy4(results : List[Map[String,JsValue]])(lst: List[Map[String,JsValue]]): List[Map[String, JsValue]] ={
        ((results ::: lst).groupBy{ x =>
            (x.get("Panel_ID").get,x.get("Date").get,x.get("City").get,x.get("Product"))
        }.map{ y =>
            val Salessum = y._2.map(z => z.get("f_sales").get.as[Double]).sum
            val Unitssum = y._2.map(x => x.get("f_units").get.as[Double]).sum
            Map(
                "Panel_ID" -> toJson(y._1._1),
                "Date" ->toJson(y._1._2),
                "City" -> toJson(y._1._3),
                "Product" -> toJson(y._1._4),
                "f_sales" -> toJson(Salessum),
                "f_units" -> toJson(Unitssum)
            )
        }) toList
    }
}
