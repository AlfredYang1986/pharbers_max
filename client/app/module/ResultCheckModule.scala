package module

import com.mongodb.casbah.Imports.DBObject
import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.common.alDao.data_connection
import com.pharbers.aqll.pattern.{CommonMessage, CommonModule, MessageDefines, ModuleTrait}
import com.pharbers.aqll.common.alDate.scala.alDateOpt
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import module.common.alRestDate
import module.common.alCallHttp
import scala.collection.mutable.ListBuffer
import com.pharbers.aqll.common.alErrorCode.alErrorCode._

object ResultCheckModuleMessage {
	sealed class msg_resultCheckBase extends CommonMessage
	case class msg_linechart(data : JsValue) extends msg_resultCheckBase
	case class msg_histogram(data : JsValue) extends msg_resultCheckBase
}

object ResultCheckModule extends ModuleTrait {
	import ResultCheckModuleMessage._
	import controllers.common.default_error_handler.f
	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModule) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_linechart(data) => msg_linechart_func(data)
		case msg_histogram(data) => msg_histogram_func(data)
		case _ => ???
	}

	def msg_linechart_func(data : JsValue)(implicit error_handler : String => JsValue, cm: CommonModule) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val company = (data \ "company").asOpt[String].getOrElse("")
			val market = (data \ "market").asOpt[String].getOrElse("")
			val date = (data \ "date").asOpt[String].getOrElse("")
			val uuid = queryUUID(company)
			val database = cm.modules.get.get("db").get.asInstanceOf[data_connection]
			uuid match {
				case None => throw new Exception("warn uuid does not exist")
				case _ => {
					val result = lsttoJson(queryNearTwelveMonth(database,company,market,date,s"$company${uuid.get}"),1)
					(successToJson(result), None)
				}
			}
		} catch {
			case ex: Exception =>	(None, Some(error_handler(ex.getMessage())))
		}
	}

	def msg_histogram_func(data : JsValue)(implicit error_handler : String => JsValue, cm: CommonModule) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val company = (data \ "company").asOpt[String].getOrElse("")
			val market = (data \ "market").asOpt[String].getOrElse("")
			val date = (data \ "date").asOpt[String].getOrElse("")
			val uuid = queryUUID(company)
			val database = cm.modules.get.get("db").get.asInstanceOf[data_connection]
			uuid match {
				case None => throw new Exception("warn uuid does not exist")
				case _ => {
					val temp_coll = s"$company${uuid.get}"
					val cur_top6 = queryCELData(database,company,market,date,temp_coll,"cur")(None,None)
					val ear_top6 = queryCELData(database,company,market,date,temp_coll,"ear")(cur_top6._2,cur_top6._3)
					val las_top6 = queryCELData(database,company,market,date,temp_coll,"las")(cur_top6._2,cur_top6._3)
					val result = toJson(Map("cur_top6" -> lsttoJson(cur_top6._1,2),"ear_top6" -> lsttoJson(ear_top6._1,2),"las_top6" -> lsttoJson(las_top6._1,2)))
					(successToJson(result), None)
				}
			}
		} catch {
			case ex: Exception => (None, Some(error_handler(ex.getMessage())))
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
			case "cur" => alDateOpt.MMyyyy2Long(date)
			case "ear" => alDateOpt.MMyyyy2EarlyLong(date)
			case "las" => alDateOpt.MMyyyy2LastLong(date)
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
		val cur_date = alDateOpt.MMyyyy2yyyyMM(date)
		val date_lst_str = alRestDate.diff12Month(cur_date)
		val query = MongoDBObject("Market" -> market,"Date" -> MongoDBObject("$in" -> alDateOpt.ArrayDate2ArrayTimeStamp(alRestDate.diff12Month(cur_date))))
		database.getCollection(temp_coll).count() match {
			case 0 => {
				val list_map = database.getCollection(company).find(query).sort(MongoDBObject("Date" -> 1)).map(x =>
					Map("Date" -> alDateOpt.Timestamp2yyyyMM(x.get("Date").asInstanceOf[Number].longValue()),
						"f_sales" -> x.get("f_sales")
					)).toList
				matchDataByDate(date_lst_str,SumByDate(list_map) :: Nil)
			}
			case _ => {
				val temp_list_map = database.getCollection(temp_coll).find(query).sort(MongoDBObject("Date" -> 1)).map(x =>
					Map("Date" -> alDateOpt.Timestamp2yyyyMM(x.get("Date").asInstanceOf[Number].longValue()),
						"f_sales" -> x.get("f_sales")
					)).toList
				val fina_list_map = database.getCollection(company).find(query).sort(MongoDBObject("Date" -> 1)).map(x =>
					Map("Date" -> alDateOpt.Timestamp2yyyyMM(x.get("Date").asInstanceOf[Number].longValue()),
						"f_sales" -> x.get("f_sales")
					)).toList
				matchDataByDate(date_lst_str,SumByDate(temp_list_map) :: SumByDate(fina_list_map) :: Nil)
			}
		}
	}

	/**
		* @author liwei
		* @param lst
		* @return
		*/
	def SumByDate(lst: List[Map[String,Any]]): List[Map[String,Any]] ={
		var date = ""
		var f_sales_sum = 0.0
		val n_lst = new ListBuffer[Map[String,Any]]()
		lst match {
			case i if i.size.equals(0) => Nil
			case i if i.size.equals(1) => lst	// TODO : 完善只有一条数据时有可能遗漏Sum和append的情况
			case _ => {
				var num = 0
				lst.foreach{x =>
					val o_date = x.get("Date").get.asInstanceOf[String]
					val o_sales = x.get("f_sales").get.asInstanceOf[Number].doubleValue()
					num = num + 1
					date match {
						case "" => {
							date = o_date
							f_sales_sum = f_sales_sum + o_sales
						}
						case i if i.equals(o_date) => {
							f_sales_sum = f_sales_sum + o_sales
						}
						case i if !i.equals(o_date) => {
							n_lst.append(Map("Date" -> date, "f_sales" -> f_sales_sum))
							num match {	// TODO : 完善最后一条数据有可能遗漏Sum和append的情况
								case n if n.equals(lst.size) => n_lst.append(Map("Date" -> o_date, "f_sales" -> o_sales))
								case n if !n.equals(lst.size) => {
									date = o_date
									f_sales_sum = 0.0
								}
							}
						}
					}
				}
				n_lst.toList
			}
		}
	}

	def queryUUID(company: String): Option[String] = {
		val uuidjson = alCallHttp("queryUUID",toJson(Map("company" -> toJson(company)))).call
		Some((uuidjson \ "result").asOpt[String].get)
	}

	def richDateArr(arr: Array[String]): List[Map[String,Any]] = {
		arr.map(x => Map("Date" -> x,"f_sales" -> 0.0)).toList
	}

	def matchDataByDate(arr: Array[String],lst: List[List[Map[String,Any]]]): List[Map[String,Any]] = {
		val date_lst = richDateArr(arr)

		val temp_head_lst = date_lst map { x =>
			val obj = lst.head.find(y => y.get("Date").get.equals(x.get("Date").get))
			obj match {
				case None => x
				case _ => obj.get
			}
		}
		val temp_tail_lst: List[Map[String,Any]] = Nil
		temp_tail_lst match {
			case Nil => temp_head_lst
			case _ => {
//				date_lst map { x =>
//					val obj = lst.tail.head.find(y => y.get("Date").get.equals(x.get("Date").get))
//					obj match {
//						case None => x
//						case _ => obj.get
//					}
//				}

				temp_tail_lst map { x =>
					val obj = temp_head_lst.find(y => y.get("Date").get.equals(x.get("Date").get))
					obj match {
						case None => x
						case _ => obj.get
					}
				}
			}
		}
	}

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