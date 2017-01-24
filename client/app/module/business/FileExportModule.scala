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
import java.util.{Calendar, Date}
import com.mongodb.DBObject
import com.pharbers.aqll.util.GetProperties

import scala.collection.immutable.List
import com.pharbers.aqll.util.file.csv.scala._

import scala.collection.mutable.ListBuffer

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
		println("1")
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

        /**
          * 所有的print都应该变为logger，并生成splunk可分析的文件为最佳
          * 计算的log，导出的log，应该重新生成一个新的数据导出源
          */
        val connectionName = (data \ "company").asOpt[String].get
		try {
			val datatype = (data \ "datatype").asOpt[String].get
            /**
              * 在这个类中不应有读文件的路径的逻辑
              * 文件路径的出来应该封装在一个properties config读取的类中
              */
			var exportpath = GetProperties.loadProperties("File.properties").getProperty("Export_File")
			val file : File = new File(exportpath+datatype+".csv")

            /**
              * 文件的创建个管理也同理
              * 这个地方有一个严重的问题
              * 当多用户同时访问时会参生同名，这个时候文件造成多线程冲突，每一个线程都不能完成操作
              */
            if(file.exists()){file.createNewFile()}
			val writer = CSVWriter.open(file,"GBK")
            /**
              * 以下应该写为配置文件，最好单独提出来作为一个config类别，以应付以后的扩展
              */
			datatype match {
				case "省份数据" =>
					writer.writeRow(List("年","月","区域","省份","最小产品单位（标准_中文）","最小产品单位（标准_英文）","生产厂家（标准_中文）","生产厂家（标准_英文）","通用名（标准_中文）","通用名（标准_英文","商品名（标准_中文）","商品名（标准_英文）","剂型（标准_中文）","剂型（标准_英文）","药品规格（标准_中文）","药品规格（标准_英文）","包装数量（标准_中文）","包装数量（标准_英文）","SKU（标准_中文）","SKU（标准_英文）","市场I（标准_中文）","市场I（标准_英文）","市场II（标准_中文）","市场II（标准_英文）","市场III（标准_中文）","市场III（标准_英文）","Value（金额）","Volume（数量）"))
				case "城市数据" =>
					writer.writeRow(List("年","月","区域","省份","城市","城市级别","最小产品单位（标准_中文）","最小产品单位（标准_英文）","生产厂家（标准_中文）","生产厂家（标准_英文）","通用名（标准_中文）","通用名（标准_英文","商品名（标准_中文）","商品名（标准_英文）","剂型（标准_中文）","剂型（标准_英文）","药品规格（标准_中文）","药品规格（标准_英文）","包装数量（标准_中文）","包装数量（标准_英文）","SKU（标准_中文）","SKU（标准_英文）","市场I（标准_中文）","市场I（标准_英文）","市场II（标准_中文）","市场II（标准_英文）","市场III（标准_中文）","市场III（标准_英文）","Value（金额）","Volume（数量）"))
				case "医院数据" =>
					writer.writeRow(List("年","月","区域","省份","城市","城市级别","医院","医院级别","最小产品单位（标准_中文）","最小产品单位（标准_英文）","生产厂家（标准_中文）","生产厂家（标准_英文）","通用名（标准_中文）","通用名（标准_英文","商品名（标准_中文）","商品名（标准_英文）","剂型（标准_中文）","剂型（标准_英文）","药品规格（标准_中文）","药品规格（标准_英文）","包装数量（标准_中文）","包装数量（标准_英文）","SKU（标准_中文）","SKU（标准_英文）","市场I（标准_中文）","市场I（标准_英文）","市场II（标准_中文）","市场II（标准_英文）","市场III（标准_中文）","市场III（标准_英文）","Value（金额）","Volume（数量）"))
			}

            /**
              * config 文字
              */
            val order = "Timestamp"
			var first = 0
			var step = 10000
			var cache = false			//smarty- caching false
			var hospdata = List(Map("" -> toJson("")))
			var miniprod = List(Map("" -> toJson("")))
			val sum = (from db() in connectionName where $and(conditions)).count(_data_connection_cores)
			while (first < sum) {
				println("------------")
				val r = (from db() in connectionName where $and(conditions)).selectSkipTop(first)(step)(order)(finalResultJsValue1(_))(_data_connection_cores).toList
				println(r.size)
				if(!cache){
					hospdata = (from db() in "HospitalInfo").select(hospitalJsValue(_))(_data_connection_cores).toList
					miniprod = (from db() in "MinimumProductInfo").select(miniProductJsValue(_))(_data_connection_cores).toList
					cache = true
				}

				val hosps = r map { x => x.++(hospdata.asInstanceOf[List[Map[String,JsValue]]].find(y => y.get("Pha_Code").get.asOpt[String].get.equals(x.get("Hospital").get.asOpt[String].get)).get) }
				val prods = hosps map { x => x.++(miniprod.asInstanceOf[List[Map[String,JsValue]]].find(y => y.get("MC").get.asOpt[String].get.equals(x.get("ProductMinunt").get.asOpt[String].get)).get) }

				prods.foreach{ x =>
					val lb : ListBuffer[AnyRef] = ListBuffer[AnyRef]()

                    /**
                      * 提出来有函数来完成，工作几年了，还写这样初学者的复制粘贴的代码
                      */
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
				if(sum - first < step){
					step = sum - first
				}else{
					first += step
				}
			}

			writer.close()
			var format : SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
			println(format.format(new Date()))
            /**
              * 导出名字和文件生成的名字是不一样的，是不是逻辑有问题
              */
			(Some(Map("finalResult" -> toJson(datatype+".csv"))), None)
		} catch {
			case ex : Exception => (None, Some(error_handler(ex.getMessage().toInt)))
		}
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