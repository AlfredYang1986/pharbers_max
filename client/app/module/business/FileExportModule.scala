package module.business

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.pharbers.aqll.pattern.ModuleTrait
import com.pharbers.aqll.pattern.MessageDefines
import com.pharbers.aqll.pattern.CommonMessage
import java.io.{BufferedWriter, File, FileOutputStream, OutputStreamWriter}

import com.pharbers.aqll.util.dao.from
import com.mongodb.casbah.Imports.{$and, _}
import com.pharbers.aqll.util.dao._data_connection_cores
import java.text.SimpleDateFormat
import java.util.Calendar
import scala.xml._
import java.util.Date

import com.mongodb.DBObject
import com.mongodb.casbah.commons.MongoDBObject

import scala.collection.immutable.List
import com.pharbers.aqll.util.file.csv.scala._
import com.pharbers.aqll.util.file.CsvHelper

import scala.collection.mutable.ListBuffer

object FileExportModuleMessage {
	sealed class msg_fileexportBase extends CommonMessage
	case class msg_finalresult1(data : JsValue) extends msg_fileexportBase
    /*case class msg_finalresult2(data : JsValue) extends msg_fileexportBase
    case class msg_finalresult3(data : JsValue) extends msg_fileexportBase
    case class msg_expotresult1(data : JsValue) extends msg_fileexportBase*/
}
object FileExportModule extends ModuleTrait{
	import FileExportModuleMessage._
	import controllers.common.default_error_handler.f
	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]]) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_finalresult1(data) => msg_finalresult_func(data)
        /*case msg_finalresult2(data) => msg_hospitalresult_func(data)(pr)
		case msg_finalresult3(data) => msg_miniproductresult_func(data)(pr)
		case msg_expotresult1(data) => msg_exportresult_func(data)(pr)*/
		case _ => ???
	}

	def msg_finalresult_func(data : JsValue)(implicit error_handler : Int => JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		println("query result start.")
		var format : SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
		println(format.format(new Date()))
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
		val connectionName = (data \ "company").asOpt[String].get
		try {
			val cursor = (from db() in connectionName where $and(conditions)).selectCursor(null)(_data_connection_cores)
			val lst : ListBuffer[Map[String,JsValue]] =  ListBuffer[Map[String,JsValue]]()
			while (cursor.hasNext){
				var obj : DBObject = cursor.next().asInstanceOf[DBObject]
				var finalresult1 = finalResultJsValue1(obj)
				lst.append(finalresult1)
			}
			println("1")
			var result1 = msg_hospitalresult_func(lst.toList)
			var result2 = msg_miniproductresult_func(result1)
			(Some(Map("finalResult" -> toJson(msg_exportresult_func(data,result2)))), None)
		} catch {
			case ex : Exception => (None, Some(error_handler(ex.getMessage().toInt)))
		}
	}

	def msg_hospitalresult_func(pr : List[Map[String, JsValue]]) : List[Map[String,JsValue]] = {
		val phacodes = pr.map(x => x.get("Hospital")).map(x => x.get.asOpt[String].get).distinct
		val hospitalinfos = (from db() in "HospitalInfo" where ("Pha_Code" $in phacodes)).select(hospitalJsValue(_))(_data_connection_cores).toList
		val hosps = pr map { x =>
			x.++(hospitalinfos.asInstanceOf[List[Map[String,JsValue]]].find(y => y.get("Pha_Code").get.asOpt[String].get.equals(x.get("Hospital").get.asOpt[String].get)).get)
		}
		println("2")
		hosps
	}

	def msg_miniproductresult_func(pr : List[Map[String, JsValue]]) : List[Map[String, JsValue]] = {
		val miniproducts = pr.map(x => x.get("ProductMinunt")).map(x => x.get.asOpt[String].get).distinct
		val miniproductinfos = (from db() in "MinimumProductInfo" where ("MC" $in miniproducts)).select(miniProductJsValue(_))(_data_connection_cores).toList
		val prods = pr map { x =>
			x.++(miniproductinfos.asInstanceOf[List[Map[String,JsValue]]].find(y => y.get("MC").get.asOpt[String].get.equals(x.get("ProductMinunt").get.asOpt[String].get)).get)
		}
		println("3")
		prods
	}

	def msg_exportresult_func(data : JsValue,pr : List[Map[String, JsValue]]) : String = {
		 val datatype = (data \ "datatype").asOpt[String].get
		 var filepath = "D:/SourceData/Download/"+datatype+".csv"
		 val file : File = new File(filepath)
		 if(file.exists()){file.createNewFile()}
		 val writer = CSVWriter.open(file,"GBK")

		datatype match {
			case "省份数据" => writer.writeRow(List("年","月","区域","省份","最小产品单位（标准_中文）","最小产品单位（标准_英文）","生产厂家（标准_中文）","生产厂家（标准_英文）","通用名（标准_中文）","通用名（标准_英文","商品名（标准_中文）","商品名（标准_英文）","剂型（标准_中文）","剂型（标准_英文）","药品规格（标准_中文）","药品规格（标准_英文）","包装数量（标准_中文）","包装数量（标准_英文）","SKU（标准_中文）","SKU（标准_英文）","市场I（标准_中文）","市场I（标准_英文）","市场II（标准_中文）","市场II（标准_英文）","市场III（标准_中文）","市场III（标准_英文）","Value（金额）","Volume（数量）"))
			case "城市数据" => writer.writeRow(List("年","月","区域","省份","城市","城市级别","最小产品单位（标准_中文）","最小产品单位（标准_英文）","生产厂家（标准_中文）","生产厂家（标准_英文）","通用名（标准_中文）","通用名（标准_英文","商品名（标准_中文）","商品名（标准_英文）","剂型（标准_中文）","剂型（标准_英文）","药品规格（标准_中文）","药品规格（标准_英文）","包装数量（标准_中文）","包装数量（标准_英文）","SKU（标准_中文）","SKU（标准_英文）","市场I（标准_中文）","市场I（标准_英文）","市场II（标准_中文）","市场II（标准_英文）","市场III（标准_中文）","市场III（标准_英文）","Value（金额）","Volume（数量）"))
			case "医院数据" => writer.writeRow(List("年","月","区域","省份","城市","城市级别","医院","医院级别","最小产品单位（标准_中文）","最小产品单位（标准_英文）","生产厂家（标准_中文）","生产厂家（标准_英文）","通用名（标准_中文）","通用名（标准_英文","商品名（标准_中文）","商品名（标准_英文）","剂型（标准_中文）","剂型（标准_英文）","药品规格（标准_中文）","药品规格（标准_英文）","包装数量（标准_中文）","包装数量（标准_英文）","SKU（标准_中文）","SKU（标准_英文）","市场I（标准_中文）","市场I（标准_英文）","市场II（标准_中文）","市场II（标准_英文）","市场III（标准_中文）","市场III（标准_英文）","Value（金额）","Volume（数量）"))
		}

		 pr.foreach{ x =>
			 val lb : ListBuffer[AnyRef] = ListBuffer[AnyRef]()
			 lb.append(x.get("Year").get)
			 lb.append(x.get("Month").get)
			 datatype match {
				 case "省份数据" =>
					 lb.append(x.get("Region_Name").get)
					 lb.append(x.get("Province_Name").get)
				 case "城市数据" =>
					 lb.append(x.get("Region_Name").get)
					 lb.append(x.get("Province_Name").get)
					 lb.append(x.get("City_Name").get)
					 lb.append(x.get("City_Level").get)
				 case "医院数据" =>
					 lb.append(x.get("Region_Name").get)
					 lb.append(x.get("Province_Name").get)
					 lb.append(x.get("City_Name").get)
					 lb.append(x.get("City_Level").get)
					 lb.append(x.get("Hosp_Name").get)
					 lb.append(x.get("Hosp_Level").get)
			 }
			 lb.append(x.get("MC").get)
			 lb.append(x.get("ME").get)
			 lb.append(x.get("QC").get)
			 lb.append(x.get("QE").get)
			 lb.append(x.get("YC").get)
			 lb.append(x.get("YE").get)
			 lb.append(x.get("SC").get)
			 lb.append(x.get("SE").get)
			 lb.append(x.get("JC").get)
			 lb.append(x.get("JE").get)
			 lb.append(x.get("GC").get)
			 lb.append(x.get("GE").get)
			 lb.append(x.get("LC").get)
			 lb.append(x.get("LE").get)
			 lb.append(x.get("KC").get)
			 lb.append(x.get("KE").get)
			 lb.append(x.get("Market").get)
			 lb.append(x.get("Market").get)
			 lb.append(x.get("Market").get)
			 lb.append(x.get("Market").get)
			 lb.append(x.get("Market").get)
			 lb.append(x.get("Market").get)
			 lb.append(x.get("Sales").get)
			 lb.append(x.get("Units").get)
			 writer.writeRow(lb.toList)
		}
		writer.close()
		var format : SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
		println(format.format(new Date()))
		/*val bw: BufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "GBK"))
		bw.write("年,月,区域,省份,城市,城市级别,医院,医院级别,最小产品单位（标准_中文）,最小产品单位（标准_英文）,生产厂家（标准_中文）,生产厂家（标准_英文）,通用名（标准_中文）,通用名（标准_英文,商品名（标准_中文）,商品名（标准_英文）,剂型（标准_中文）,剂型（标准_英文）,药品规格（标准_中文）,药品规格（标准_英文）,包装数量（标准_中文）,包装数量（标准_英文）,SKU（标准_中文）,SKU（标准_英文）,市场I（标准_中文）,市场I（标准_英文）,市场II（标准_中文）,市场II（标准_英文）,市场III（标准_中文）,市场III（标准_英文）,Value（金额）,Volume（数量）")
		bw.newLine()
		pr.foreach { x =>
			var sb : StringBuffer = new StringBuffer()
			sb.append(x.get("Year").get).append(",")
			sb.append(x.get("Month").get).append(",")
			sb.append(x.get("Region_Name").get).append(",")
			sb.append(x.get("Province_Name").get).append(",")
			sb.append(x.get("City_Name").get).append(",")
			sb.append(x.get("City_Level").get).append(",")
			sb.append(x.get("Hosp_Name").get).append(",")
			sb.append(x.get("Hosp_Level").get).append(",")
			sb.append(x.get("MC").get).append(",")
			sb.append(x.get("ME").get).append(",")
			sb.append(x.get("QC").get).append(",")
			sb.append(x.get("QE").get).append(",")
			sb.append(x.get("YC").get).append(",")
			sb.append(x.get("YE").get).append(",")
			sb.append(x.get("SC").get).append(",")
			sb.append(x.get("SE").get).append(",")
			sb.append(x.get("JC").get).append(",")
			sb.append(x.get("JE").get).append(",")
			sb.append(x.get("GC").get).append(",")
			sb.append(x.get("GE").get).append(",")
			sb.append(x.get("LC").get).append(",")
			sb.append(x.get("LE").get).append(",")
			sb.append(x.get("KC").get).append(",")
			sb.append(x.get("KE").get).append(",")
			sb.append(x.get("Market").get).append(",")
			sb.append(x.get("Market").get).append(",")
			sb.append(x.get("Market").get).append(",")
			sb.append(x.get("Market").get).append(",")
			sb.append(x.get("Market").get).append(",")
			sb.append(x.get("Market").get).append(",")
			sb.append(x.get("Sales").get).append(",")
			sb.append(x.get("Units").get)
			bw.write(sb.toString)
			bw.newLine()
		}
		bw.close()*/
		println("4")
		"OK"
	}

	def finalResultJsValue1(obj : DBObject) : Map[String,JsValue] = {
		val timeDate = Calendar.getInstance
		timeDate.setTimeInMillis(obj.get("Timestamp").asInstanceOf[Long])
		Map(
			"Year" -> toJson(timeDate.get(Calendar.YEAR)),
			"Month" -> toJson((timeDate.get(Calendar.MONTH))+1),
			"Hospital" -> toJson(obj.get("Hospital").asInstanceOf[String]),
			"ProductMinunt" -> toJson(obj.get("ProductMinunt").asInstanceOf[String]),
			/*"Market_Code1_Ch" -> toJson(obj.get("Market").asInstanceOf[String]),
			"Market_Code1_En" -> toJson(obj.get("Market").asInstanceOf[String]),
			"Market_Code2_Ch" -> toJson(obj.get("Market").asInstanceOf[String]),
			"Market_Code2_En" -> toJson(obj.get("Market").asInstanceOf[String]),
			"Market_Code3_Ch" -> toJson(obj.get("Market").asInstanceOf[String]),
			"Market_Code3_En" -> toJson(obj.get("Market").asInstanceOf[String]),*/
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