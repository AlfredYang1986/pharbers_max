package module.business

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.pharbers.aqll.pattern.ModuleTrait
import com.pharbers.aqll.pattern.MessageDefines
import com.pharbers.aqll.pattern.CommonMessage
import java.io.File

import com.pharbers.aqll.util.dao.from
import com.mongodb.casbah.Imports.{$and, _}
import com.pharbers.aqll.util.dao._data_connection_cores
import java.text.SimpleDateFormat
import java.util.{Calendar, UUID}

import com.mongodb.{DBObject}
import com.pharbers.aqll.util.GetProperties

import scala.collection.immutable.List
import com.pharbers.aqll.util.file.csv.scala._

import scala.collection.mutable.ListBuffer
import com.pharbers.aqll.pattern.LogMessage._

object FileExportModuleMessage {
	sealed class msg_fileexportBase extends CommonMessage
	case class msg_finalresult1(data : JsValue) extends msg_fileexportBase
}

object FileExportModule extends ModuleTrait{
	import FileExportModuleMessage._
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
				Some("Timestamp" $gte start $lte end)
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
			var con = conditionsAcc(Nil, "Timestamp" :: Nil, dateListConditions(x => x.asOpt[List[String]]))
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
		val datatype = (data \ "datatype").asOpt[String].get
		val fileName = UUID.randomUUID + ".csv"
		val file : File = new File(GetProperties.Client_Export_FilePath+fileName)
		if(!file.exists()){file.createNewFile()}
		val writer = CSVWriter.open(file,"GBK")
		writer.writeRow(getFieldContent(datatype,"ch"))
		val order = "Timestamp"
		var first = 0
		var step = 10000
		var iscache = false			//smarty- caching false
		var hospdata = List(Map("" -> toJson("")))
		var miniprod = List(Map("" -> toJson("")))
		var temp: List[Map[String,JsValue]] = List.empty
		val sum = (from db() in connectionName where $and(conditions)).count(_data_connection_cores)
		while (first < sum) {
			val r = (from db() in connectionName where $and(conditions)).selectSkipTop(first)(step)(order)(finalResultJsValue1(_))(_data_connection_cores).toList
			if(!iscache){
				hospdata = (from db() in "HospitalInfo").select(hospitalJsValue(_))(_data_connection_cores).toList
				miniprod = (from db() in "MinimumProductInfo").select(miniProductJsValue(_))(_data_connection_cores).toList
				iscache = true
			}
			val hosps = r map { x => x.++(hospdata.asInstanceOf[List[Map[String,JsValue]]].find(y => y.get("Pha_Code").get.asOpt[String].get.equals(x.get("Hospital").get.asOpt[String].get)).get) }
			datatype match {
				case "省份数据" => {
					temp = GroupByProvinceFunc.apply(hosps)(temp)
				}
				case "城市数据" => {
					temp = GroupByCityFunc(hosps)(temp)
				}
				case "医院数据" => {
					writeConFunc(hosps, miniprod, datatype, writer)
				}
			}
            if(sum - first < step){
                step = sum - first
            }
            first += step
            writing_log(data,"FileExportModule",first,sum)
		}
		if(datatype.equals("省份数据") || datatype.equals("城市数据")){
			writeConFunc(temp, miniprod, datatype, writer)
		}
		writer.close()
		fileName
	}

	def writeConFunc(temp : List[Map[String,JsValue]], miniprod : List[Map[String,JsValue]], datatype : String, writer : CSVWriter) {
		val prods = temp map { x => x.++(miniprod.asInstanceOf[List[Map[String,JsValue]]].find(y => y.get("MC").get.asOpt[String].get.equals(x.get("ProductMinunt").get.asOpt[String].get)).get) }
		var field = getFieldContent(datatype,"en")
		prods.foreach{ x =>
			val lb : ListBuffer[AnyRef] = ListBuffer[AnyRef]()
			field.foreach(y => lb.append(x.get(y).get))
			writer.writeRow(lb.toList)
		}
	}

	def getFieldContent(fn : String , str : String) : List[String] = {
		val someXml : String = "xml/FileExport.xml"
		var header : List[String] = ((xml.XML.loadFile(someXml) \ "header" \ str).map (x => x.text)).toList
		var tail : List[String] = ((xml.XML.loadFile(someXml) \ "tail" \ str).map (x => x.text)).toList
		var province = ((xml.XML.loadFile(someXml) \ "body" \ "province" \ str).map (x => x.text)).toList
		var city = ((xml.XML.loadFile(someXml) \ "body" \ "city" \ str).map (x => x.text)).toList
		var hospital = ((xml.XML.loadFile(someXml) \ "body" \ "hospital" \ str).map (x => x.text)).toList
		var body : List[String] = fn match {
			case "省份数据" => province
			case "城市数据" => province ++ city
			case "医院数据" => province ++ city ++ hospital
		}
		header ++ body ++ tail
	}

	def finalResultJsValue1(obj : DBObject) : Map[String,JsValue] = {
		val timeDate = Calendar.getInstance
		timeDate.setTimeInMillis(obj.get("Timestamp").asInstanceOf[Long])
		Map(
			"Year" -> toJson(timeDate.get(Calendar.YEAR)),
			"Month" -> toJson((timeDate.get(Calendar.MONTH))+1),
			"Hospital" -> toJson(obj.get("Hospital").asInstanceOf[String]),
			"ProductMinunt" -> toJson(obj.get("ProductMinunt").asInstanceOf[String]),
			"Market" -> toJson(obj.get("Market").asInstanceOf[String]),
			"Sales" -> toJson(obj.get("Sales").asInstanceOf[Double]),
			"Units" -> toJson(obj.get("Units").asInstanceOf[Double])
		)
	}

	def hospitalJsValue(obj : DBObject) : Map[String,JsValue] = {
		Map(
			"Region_Name" -> toJson(obj.get("Region").asInstanceOf[String]),
			"Province_Name" -> toJson(obj.get("Province_Name").asInstanceOf[String]),
			"City_Name" -> toJson(obj.get("City_Name").asInstanceOf[String]),
			"City_Level" -> toJson(obj.get("City_Tier").asInstanceOf[Number].intValue()),
			"Hosp_Name" -> toJson(obj.get("Hosp_Name").asInstanceOf[String]),
			"Pha_Code" -> toJson(obj.get("Pha_Code").asInstanceOf[String]),
			"Hosp_Level" -> toJson(obj.get("Hosp_level").asInstanceOf[String])
		)
	}

	def miniProductJsValue(obj : DBObject) : Map[String,JsValue] = {
		Map(
			"MC" -> toJson(obj.get("MC").asInstanceOf[String]),
			"ME" -> toJson(obj.get("ME").asInstanceOf[String]),
			"QC" -> toJson(obj.get("QC").asInstanceOf[String]),
			"QE" -> toJson(obj.get("QE").asInstanceOf[String]),
			"YC" -> toJson(obj.get("YC").asInstanceOf[String]),
			"YE" -> toJson(obj.get("YE").asInstanceOf[String]),
			"SC" -> toJson(obj.get("SC").asInstanceOf[String]),
			"SE" -> toJson(obj.get("SE").asInstanceOf[String]),
			"JC" -> toJson(obj.get("JC").asInstanceOf[String]),
			"JE" -> toJson(obj.get("JE").asInstanceOf[String]),
			"GC" -> toJson(obj.get("GC").asInstanceOf[String]),
			"GE" -> toJson(obj.get("GE").asInstanceOf[String]),
			"LC" -> toJson(obj.get("LC").asInstanceOf[Number].intValue()),
			"LE" -> toJson(obj.get("LE").asInstanceOf[Number].intValue()),
			"KC" -> toJson(obj.get("KC").asInstanceOf[String]),
			"KE" -> toJson(obj.get("KE").asInstanceOf[String])
		)
	}
}