package module.business

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import java.util.Date
import java.util.Calendar
import com.pharbers.aqll.pattern.ModuleTrait
import com.pharbers.aqll.pattern.MessageDefines
import com.pharbers.aqll.pattern.CommonMessage
import play.api.libs.json.JsObject
import com.pharbers.aqll.util.dao.from
import com.mongodb.casbah.Imports._
import com.pharbers.aqll.util.dao._data_connection_cores

object ResultQueryModuleMessage {
	sealed class msg_resultqueryBase extends CommonMessage
	case class msg_finalresult(data : JsValue) extends msg_resultqueryBase
	case class msg_hospitalresult(data : JsValue) extends msg_resultqueryBase
	case class msg_miniproductresult(data : JsValue) extends msg_resultqueryBase
}

object ResultQueryModule extends ModuleTrait {
	import ResultQueryModuleMessage._
	import controllers.common.default_error_handler.f
	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]]) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_finalresult(data) => msg_finalresult_func(data)
		case msg_hospitalresult(data) => msg_hospitalresult_func(data)(pr)
		case msg_miniproductresult(data) => msg_miniproductresult_func(data)(pr)
		case _ => ???
	}
	
	def msg_finalresult_func(data : JsValue)(implicit error_handler : Int => JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
	    
	    def dateListConditions(getter : JsValue => Any)(key : String, value : JsValue) : Option[DBObject] = getter(value) match {
          case None => None
          case Some(x) => {
              import java.text.SimpleDateFormat
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
	    
	    val currentPage = (data \ "currentPage").asOpt[Int].map (x => x).getOrElse(3)
	    val take = 10
        val skip = ((currentPage-1)*take)
        val order = "Timestamp"
        
        try {
            val r = (from db() in "FinalResult" where $and(conditions)).selectSkipTop(skip)(take)(order)(finalResultJsValue(_))(_data_connection_cores).toList
            val n = (from db() in "FinalResult" where $and(conditions)).count(_data_connection_cores)
                    
            (Some(Map("finalResult" -> toJson(r), "page" -> toJson(page(currentPage,take,skip,n)))), None)
		} catch {
			case ex : Exception => (None, Some(error_handler(ex.getMessage().toInt)))
		}
	}
	
	def msg_hospitalresult_func(data : JsValue)(pr : Option[Map[String, JsValue]])(implicit error_handler : Int => JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
	    import com.pharbers.aqll.pattern.ParallelMessage.f
	    try {
	        val phacodes = pr.get.map(_._2).map(x => x.\\("Hospital")).head.map(x => x.asOpt[String].get)
	        val hospitalinfos = (from db() in "HospitalInfo" where ("Pha_Code" $in phacodes)).select(hospitalJsValue(_))(_data_connection_cores).toList
	        val hosps = pr.get.get("finalResult").map ( x => x.as[List[Map[String,JsValue]]]).get map { x =>
	            var tmp:Map[String, JsValue] = Map.empty
	            val flag = hospitalinfos.exists { y => tmp = y; x.get("Hospital").get.asOpt[String].get.equals(y.get("Pha_Code").get.asOpt[String].get) }
	            var finalhosps: Map[String,JsValue] = x
	            if(flag) {
	                finalhosps = finalhosps ++: Map("Region_Name" -> toJson(tmp.get("Region_Name").get.asOpt[String].get), 
	                                "Province_Name" -> toJson(tmp.get("Province_Name").get.asOpt[String].get), 
                                    "City_Name" -> toJson(tmp.get("City_Name").get.asOpt[String].get),
                                    "City_Level" -> toJson(tmp.get("City_Level").get),
                                    "Hosp_Name" -> toJson(tmp.get("Hosp_Name").get.asOpt[String].get),
                                    "Pha_Code" -> toJson(tmp.get("Pha_Code").get.asOpt[String].get),
                                    "Hosp_Level" -> toJson(tmp.get("Hosp_Level").get.asOpt[String].get))
	            }
	            finalhosps
	        }
	        (Some(Map("finalResult" -> toJson(hosps), "page" -> toJson(pr.get.get("page")))), None)
	    } catch {
          case ex : Exception => (None, Some(error_handler(ex.getMessage().toInt)))
        }
	}
	
	def msg_miniproductresult_func(data : JsValue)(pr : Option[Map[String, JsValue]])(implicit error_handler : Int => JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
	    import com.pharbers.aqll.pattern.ParallelMessage.f
	    try {
	        val miniproducts = pr.get.map(_._2).map(x => x.\\("ProductMinunt")).head.map(x => x.asOpt[String].get)
	        val miniproductinfos = (from db() in "MinimumProductInfo" where ("MiniProd_Name_Ch" $in miniproducts)).select(miniProductJsValue(_))(_data_connection_cores).toList
	        val prods = pr.get.get("finalResult").map ( x => x.as[List[Map[String,JsValue]]]).get map { x =>
	            var tmp:Map[String, JsValue] = Map.empty
	            val flag = miniproductinfos.exists { y => tmp = y; x.get("ProductMinunt").get.asOpt[String].get.equals(y.get("MiniProd_Name_Ch").get.asOpt[String].get) }
	            var finalprods: Map[String,JsValue] = x
	            if(flag) {
	                finalprods = finalprods ++: Map(
                                    "MiniProd_Name_Ch" -> toJson(tmp.get("MiniProd_Name_Ch").get.asOpt[String].get),
                            	    "MiniProd_Name_En" -> toJson(tmp.get("MiniProd_Name_En").get.asOpt[String].get),
                            	    "Manufacturer_Ch" -> toJson(tmp.get("Manufacturer_Ch").get.asOpt[String].get),
                            	    "Manufacturer_En" -> toJson(tmp.get("Manufacturer_En").get.asOpt[String].get),
                            	    "Drug_Ch" -> toJson(tmp.get("Drug_Ch").get.asOpt[String].get),
                            	    "Drug_En" -> toJson(tmp.get("Drug_En").get.asOpt[String].get),
                            	    "Products_Ch" -> toJson(tmp.get("Products_Ch").get.asOpt[String].get),
                            	    "Products_En" -> toJson(tmp.get("Products_En").get.asOpt[String].get),
                            	    "DosageForm_Ch" -> toJson(tmp.get("DosageForm_Ch").get.asOpt[String].get),
                            	    "DosageForm_En" -> toJson(tmp.get("DosageForm_En").get.asOpt[String].get),
                            	    "DrugSpecification_Ch" -> toJson(tmp.get("DrugSpecification_Ch").get.asOpt[String].get),
                            	    "DrugSpecification_En" -> toJson(tmp.get("DrugSpecification_En").get.asOpt[String].get),
                            	    "Package_Quantity_Ch" -> toJson(tmp.get("Package_Quantity_Ch").get),
                            	    "Package_Quantity_En" -> toJson(tmp.get("Package_Quantity_En").get),
                            	    "sku_Ch" -> toJson(tmp.get("sku_Ch").get.asOpt[String].get),
                            	    "sku_En" -> toJson(tmp.get("sku_En").get.asOpt[String].get))
	            }
	            finalprods
	        }
	        (Some(Map("finalResult" -> toJson(prods), "page" -> toJson(pr.get.get("page")))), None)
	    } catch {
          case ex : Exception => (None, Some(error_handler(ex.getMessage().toInt)))
        }
	}
	
	def page(currentPage : Int, take : Int, skip : Int, total : Int) : List[Map[String,JsValue]] = {
	    Map(
	         "startrow" -> toJson((currentPage-1)*take+1),     
	         "endrow" -> toJson(currentPage*take),
	         "currentpage" -> toJson(currentPage),
	         "totlepage" -> toJson(if(total%take==0)(total/take)else(total/take+1)),
	         "total" -> toJson(total)
	    ):: Nil
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
	
	def hospitalJsValue(x : MongoDBObject) : Map[String,JsValue] = {
	    Map(
    	    "Region_Name" -> toJson(x.getAs[String]("Region").get),
    	    "Province_Name" -> toJson(x.getAs[String]("Province_Name").get),
    	    "City_Name" -> toJson(x.getAs[String]("City_Name").get),
    	    "City_Level" -> toJson(x.getAs[Number]("City_Tier").get.longValue()),
    	    "Hosp_Name" -> toJson(x.getAs[String]("Hosp_Name").get),
    	    "Pha_Code" -> toJson(x.getAs[String]("Pha_Code").get),
    	    "Hosp_Level" -> toJson(x.getAs[String]("Hosp_level").get)
	    )
	}
	
	def miniProductJsValue(x : MongoDBObject) : Map[String,JsValue] = {
	     Map(
    	    "MiniProd_Name_Ch" -> toJson(x.getAs[String]("MiniProd_Name_Ch")),
    	    "MiniProd_Name_En" -> toJson(x.getAs[String]("MiniProd_Name_En")),
    	    "Manufacturer_Ch" -> toJson(x.getAs[String]("Manufacturer_Ch")),
    	    "Manufacturer_En" -> toJson(x.getAs[String]("Manufacturer_En")),
    	    "Drug_Ch" -> toJson(x.getAs[String]("Drug_Ch")),
    	    "Drug_En" -> toJson(x.getAs[String]("Drug_En")),
    	    "Products_Ch" -> toJson(x.getAs[String]("Products_Ch")),
    	    "Products_En" -> toJson(x.getAs[String]("Products_En")),
    	    "DosageForm_Ch" -> toJson(x.getAs[String]("DosageForm_Ch")),
    	    "DosageForm_En" -> toJson(x.getAs[String]("DosageForm_En")),
    	    "DrugSpecification_Ch" -> toJson(x.getAs[String]("DrugSpecification_Ch")),
    	    "DrugSpecification_En" -> toJson(x.getAs[String]("DrugSpecification_En")),
    	    "Package_Quantity_Ch" -> toJson(x.getAs[Number]("Package_Quantity_Ch").get.longValue()),
    	    "Package_Quantity_En" -> toJson(x.getAs[Number]("Package_Quantity_En").get.longValue()),
    	    "sku_Ch" -> toJson(x.getAs[String]("sku_Ch")),
    	    "sku_En" -> toJson(x.getAs[String]("sku_En"))
	     )
	}
}