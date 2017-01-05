package module.business

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.pharbers.aqll.pattern.ModuleTrait
import com.pharbers.aqll.pattern.MessageDefines
import com.pharbers.aqll.pattern.CommonMessage
import java.io.File

import com.pharbers.aqll.util.file.excel.CSVUtils
import com.pharbers.aqll.util.dao.from
import com.mongodb.casbah.Imports._
import com.pharbers.aqll.util.dao._data_connection_cores
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.ArrayList

object FileExportModuleMessage {
      sealed class msg_fileexportBase extends CommonMessage
	  case class msg_fileexport(data : JsValue) extends msg_fileexportBase
}

object FileExportModule extends ModuleTrait{
    import FileExportModuleMessage._
	import controllers.common.default_error_handler.f
	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]]) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_fileexport(data) => msg_fileexport_func(data)
		case _ => ???
	}
    
    def msg_fileexport_func(data : JsValue)(implicit error_handler : Int => JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
        println("写入成功")
		def dateListConditions(getter : JsValue => Any)(key : String, value : JsValue) : Option[DBObject] = getter(value) match {
			case None => None
			case Some(x) => {
				val fm = new SimpleDateFormat("MM/yyyy")
				val start = fm.parse(x.asInstanceOf[List[String]].head).getTime
				val end = fm.parse(x.asInstanceOf[List[String]].last).getTime
				Some("Timestamp" $gte start $lte end)
			}
		}

		def marketListConditions(getter : JsValue => Any)(key : String, value : JsValue) : Option[DBObject] = getter(value) match {
			case None => None
			case Some(x) => {
				val lst = x.asInstanceOf[List[String]].map { str => str }
				Some($and("Market" $in lst))
			}
		}

		def conditionsAcc(o : List[DBObject], keys : List[String], func : (String, JsValue) => Option[DBObject]) : List[DBObject] = keys match {
			case Nil => o
			case head :: lst => func(head, (data \ head)) match {
				case None => conditionsAcc(o, lst, func)
				case Some(y) => conditionsAcc(y :: o, lst, func)
			}
		}

		def conditions : List[DBObject] = {
			var con = conditionsAcc(Nil, "Timestamp" :: Nil, dateListConditions(x => x.asOpt[List[String]]))
			con = conditionsAcc(con, "market" :: Nil, marketListConditions(x => x.asOpt[List[String]]))
			con
		}

		val connectionName = (data \ "company").asOpt[String].get
		val datatype = (data \ "datatype").asOpt[String].get
		try {
			//val r = (from db() in connectionName where (conditions)).select(finalResultJsValue(_))(_data_connection_cores).toList
			//println(s"ssssss=$r")
			var file : File = new File("download/"+datatype+".csv")
			val list = new ArrayList[String]()
			list.add("省份,城市,医院")
			list.add("北京市,北京市,人民医院")
			list.add("上海市,上海市,武警医院")

			var isSuccess : Boolean = CSVUtils.exportCsv(file, list)
			println(isSuccess)
			(Some(Map("finalResult" -> toJson("ok"))), None)
		} catch {
			case ex : Exception => (None, Some(error_handler(ex.getMessage().toInt)))
		}
    }

	def finalResultJsValue(x : MongoDBObject) : Map[String,JsValue] = {
		val timeDate = Calendar.getInstance
		timeDate.setTimeInMillis(x.getAs[Number]("Timestamp").get.longValue)
		Map(
			"Year" -> toJson(timeDate.get(Calendar.YEAR)),
			"Month" -> toJson((timeDate.get(Calendar.MONTH))+1),
			"Hospital" -> toJson(x.getAs[String]("Hospital").get),
			"ProductMinunt" -> toJson(x.getAs[String]("ProductMinunt").get),
			"Market_Code1_Ch" -> toJson(x.getAs[String]("Market").get),
			"Market_Code1_En" -> toJson(x.getAs[String]("Market").get),
			"Market_Code2_Ch" -> toJson(""),
			"Market_Code2_En" -> toJson(""),
			"Market_Code3_Ch" -> toJson(""),
			"Market_Code3_En" -> toJson(""),
			"Sales" -> toJson(x.getAs[Number]("Sales").get.doubleValue),
			"Units" -> toJson(x.getAs[Number]("Units").get.doubleValue)
		)
	}
}