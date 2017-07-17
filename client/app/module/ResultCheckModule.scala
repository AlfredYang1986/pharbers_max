package module

import com.mongodb.casbah.Imports.DBObject
import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.common.alDao.data_connection
import com.pharbers.aqll.pattern.{CommonMessage, MessageDefines, ModuleTrait}
import com.pharbers.aqll.common.alDate.scala.alDateOpt
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import module.common.alNearDecemberMonth
import module.common.alCallHttp

import scala.collection.mutable.ListBuffer
import com.pharbers.aqll.common.alErrorCode.alErrorCode._
import com.pharbers.aqll.dbmodule.MongoDBModule

object ResultCheckModuleMessage {
	sealed class msg_resultCheckBase extends CommonMessage
	case class msg_linechart(data : JsValue) extends msg_resultCheckBase
	case class msg_histogram(data : JsValue) extends msg_resultCheckBase
}

object ResultCheckModule extends ModuleTrait {
	import ResultCheckModuleMessage._
	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit db: MongoDBModule): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_linechart(data) => msg_linechart_func(data)
		case msg_histogram(data) => msg_histogram_func(data)
		case _ => ???
	}

	def msg_linechart_func(data : JsValue)(implicit db: MongoDBModule): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val company = (data \ "company").asOpt[String].getOrElse("")
			val market = (data \ "market").asOpt[String].getOrElse("")
			val date = (data \ "date").asOpt[String].getOrElse("")

			queryUUID(company) match {
				case None => throw new Exception("warn uuid does not exist")
				case Some(x) => {
					val result = lsttoJson(queryNearTwelveMonth(db.cores,company,market,date,s"$company$x"),1)
					(successToJson(result), None)
				}
			}
		} catch {
			case ex: Exception => (None, Some(errorToJson(ex.getMessage())))
		}
	}

	def msg_histogram_func(data : JsValue)(implicit db: MongoDBModule): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val company = (data \ "company").asOpt[String].getOrElse("")
			val market = (data \ "market").asOpt[String].getOrElse("")
			val date = (data \ "date").asOpt[String].getOrElse("")
			val uuid = queryUUID(company)
			uuid match {
				case None => throw new Exception("warn uuid does not exist")
				case Some(x) => {
					val temp_coll = s"$company$x"
					val cur_top6 = queryCELData(db.cores,company,market,date,temp_coll,"cur")(None,None)
					val ear_top6 = queryCELData(db.cores,company,market,date,temp_coll,"ear")(cur_top6._2,cur_top6._3)
					val las_top6 = queryCELData(db.cores,company,market,date,temp_coll,"las")(cur_top6._2,cur_top6._3)
					val result = toJson(Map("cur_top6" -> lsttoJson(cur_top6._1,2),"ear_top6" -> lsttoJson(ear_top6._1,2),"las_top6" -> lsttoJson(las_top6._1,2)))
					(successToJson(result), None)
				}
			}
		} catch {
			case ex: Exception => (None, Some(errorToJson(ex.getMessage())))
		}
	}

	/**
		* @author liwei
		* @param company
		* @param market
		* @param date
		* @param temp_coll
		* @param ces
		* @param ols
		* @param olm
		* @return
		*/
	def queryCELData(database: data_connection,company: String,market: String,date: String,temp_coll: String,ces: String)(ols: Option[List[String]],olm: Option[List[Map[String,Any]]]): (List[Map[String,Any]],Option[List[String]],Option[List[Map[String,Any]]]) = {
		val query = queryDBObject(market,date,ces,ols)
		val temp_data = database.getCollection(temp_coll).find(query).sort(MongoDBObject("City" -> 1))
		val result = temp_data.size match {
			case 0 => database.getCollection(company).find(query).sort(MongoDBObject("City" -> 1))
			case _ => temp_data
		}

		val list = new ListBuffer[Map[String,Any]]()
		var city = ""
		var sales_sum = 0.0
		while (result.hasNext) {
			val obj = result.next()
			val obj_city = obj.get("City").asInstanceOf[String]
			city match {
				case i if i.equals("") => city = obj_city
				case i if i.equals(obj_city) => sales_sum = sales_sum + obj.get("f_sales").asInstanceOf[Number].doubleValue()
				case i if !i.equals(obj_city) => {
					list.append(Map("City" -> city,"f_sales" -> sales_sum))
					sales_sum = 0.0
					city = obj_city
				}
			}
		}
		var topsix_list : List[Map[String,Any]] = Nil
		var topsix_city : Option[List[String]] = None
		var topsix_citysales : Option[List[Map[String,Any]]] = None
		olm match {
			case None => {
				topsix_list = list.toList.sortBy(x => x.get("f_sales").get.asInstanceOf[Number].doubleValue()).reverse.slice(0,6)
				topsix_city = Some(topsix_list.map(x => x.get("City").get.asInstanceOf[String]))
				topsix_citysales = Some(topsix_list.map(x => Map("City" -> x.get("City").get.asInstanceOf[String],"f_sales" -> 0.0)))
			}
			case _ => {
				topsix_citysales = olm
				topsix_city = ols
				topsix_list = matchCityData(list.toList)(topsix_citysales.get)
			}
		}
		(topsix_list,topsix_city,topsix_citysales)
	}

	/**
		* @author liwei
		* @param market
		* @param date
		* @param el
		* @param list
		* @return
		*/
	def queryDBObject(market: String,date: String,el: String,list: Option[List[String]]): DBObject ={
		val ces_date = el match {
			case "cur" => alDateOpt.yyyyMM2Long(date)
			case "ear" => alDateOpt.yyyyMM2EarlyLong(date)
			case "las" => alDateOpt.yyyyMM2LastLong(date)
		}
		list match {
			case None => MongoDBObject("Market" -> market,"Date" -> MongoDBObject("$eq" -> ces_date))
			case _ => MongoDBObject("Market" -> market,"City" -> MongoDBObject("$in" -> list),"Date" -> MongoDBObject("$eq" -> ces_date))
		}
	}

	/**
		* @author liwei
		* @param company
		* @param market
		* @param date
		* @param temp_coll
		* @return
		*/
	def queryNearTwelveMonth(database: data_connection,company: String,market: String,date: String,temp_coll: String): List[Map[String,Any]] ={
		val date_lst_str = alNearDecemberMonth.diff12Month(date)
		val query = MongoDBObject("Market" -> market,"Date" -> MongoDBObject("$in" -> alDateOpt.ArrayDate2ArrayTimeStamp(alNearDecemberMonth.diff12Month(date))))
		database.getCollection(temp_coll).count() match {
			case 0 => {
				val list_map = database.getCollection(company).find(query).sort(MongoDBObject("Date" -> 1)).map(x =>
					Map("Date" -> alDateOpt.Timestamp2yyyyMM(x.get("Date").asInstanceOf[Number].longValue()),"f_sales" -> x.get("f_sales")
					)).toList
				matchDataByDate(date_lst_str,(None,Some(SumByDate(list_map))))
			}
			case _ => {
				val temp_list_map = database.getCollection(temp_coll).find(query).sort(MongoDBObject("Date" -> 1)).map(x =>
					Map("Date" -> alDateOpt.Timestamp2yyyyMM(x.get("Date").asInstanceOf[Number].longValue()),"f_sales" -> x.get("f_sales")
					)).toList
				val fina_list_map = database.getCollection(company).find(query).sort(MongoDBObject("Date" -> 1)).map(x =>
					Map("Date" -> alDateOpt.Timestamp2yyyyMM(x.get("Date").asInstanceOf[Number].longValue()),"f_sales" -> x.get("f_sales")
					)).toList
				matchDataByDate(date_lst_str,(Some(SumByDate(temp_list_map)),Some(SumByDate(fina_list_map))))
			}
		}
	}

	/**
		* @author liwei
		* @param lst
		* @return
		*/
	def SumByDate(lst: List[Map[String,Any]]): List[Map[String,Any]] = lst.groupBy(x => x.get("Date").get).map(y => Map("Date" -> y._1,"f_sales" -> y._2.map(z => z.get("f_sales").get.asInstanceOf[Number].doubleValue()).sum)).toList

	def queryUUID(company: String): Option[String] = {
		val result = alCallHttp("/queryUUID",toJson(Map("company" -> toJson(company)))).call
		val res_json = (result \ "result").get.asOpt[JsValue].get
//		val res_valu = (((res_json \ "result").get.asOpt[JsValue].get) \ "result").get.asOpt[String].get
		val res_valu = (res_json \ "result").asOpt[String].getOrElse("0")
		Some(res_valu)
	}

	def matchDataByDate(arr: Array[String],tuple_lst: (Option[List[Map[String,Any]]],Option[List[Map[String,Any]]])): List[Map[String,Any]] = {
		val date_lst = arr.map(x => Map("Date" -> x,"f_sales" -> 0.0)).toList
		tuple_lst._1 match {
			case None => {
				tuple_lst._2 match {
					case None => date_lst
					case Some(o) => getTuple_Date_Lst(date_lst,o)
				}
			}
			case Some(x) => {
				tuple_lst._2 match {
					case None => getTuple_Date_Lst(date_lst,x)
					case Some(o) => getTuple_Date_Lst(getTuple_Date_Lst(date_lst,o),getTuple_Date_Lst(date_lst,x))
				}
			}
		}
	}

	def getTuple_Date_Lst(f_lst: List[Map[String,Any]],s_lst: List[Map[String,Any]]): List[Map[String,Any]] = f_lst.map(x =>
		s_lst.find(y => y.get("Date").get.equals(x.get("Date").get)) match {
			case None => x
			case Some(o) => o
		}
	)

	def salesSumByDate(lst: List[Map[String,Any]]): List[Map[String,Any]] = {
		var date = ""
		var sales_sum = 0.0
		val list = new ListBuffer[Map[String,Any]]()
		lst foreach {x =>
			val obj_date = x.get("Date").get.asInstanceOf[String]
			val obj_sales = x.get("f_sales").get.asInstanceOf[Number].doubleValue()
			date match {
				case i if i.equals("") => date = obj_date
				case i if i.equals(obj_date) => sales_sum = sales_sum + obj_sales
				case i if !i.equals(obj_date) => {
					list.append(Map("Date" -> date,"f_sales" -> sales_sum))
					date = obj_date
					sales_sum = 0.0
				}
			}
		}
		list.toList
	}

	def matchCityData(m_data: List[Map[String,Any]])(r_data: List[Map[String,Any]]): List[Map[String,Any]] = r_data map(x => findSomeDataByCity(m_data.find(y => x.get("City").get.equals(y.get("City").get)))(x))

	def findSomeDataByCity(omap: Option[Map[String,Any]])(map: Map[String,Any]) : Map[String,Any] = omap match {
		case None => map
		case _ => omap.get
	}

	def lsttoJson(lst: List[Map[String,Any]],s : Int): JsValue = s match {
		case 1 => toJson(lst.map(x => toJson(Map("Date" -> toJson(x.get("Date").get.asInstanceOf[String]),"f_sales" -> toJson(x.get("f_sales").get.asInstanceOf[Number].doubleValue())))))
		case 2 => toJson(lst map(x => toJson(Map("City" -> toJson(x.get("City").get.asInstanceOf[String]),"f_sales" -> toJson(x.get("f_sales").get.asInstanceOf[Number].doubleValue())))))
	}
}