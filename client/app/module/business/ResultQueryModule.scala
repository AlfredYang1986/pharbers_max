package module.business

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.mongodb.casbah.Imports._
import com.pharbers.aqll.util.dao.from
import com.pharbers.aqll.util.dao._data_connection
import java.util.Date
import java.util.Calendar
import com.pharbers.aqll.pattern.ModuleTrait
import com.pharbers.aqll.pattern.MessageDefines
import com.pharbers.aqll.pattern.CommonMessage

object ResultQueryModuleMessage {
	sealed class msg_resultqueryBase extends CommonMessage
	case class msg_provincedata(data : JsValue) extends msg_resultqueryBase
}

object ResultQueryModule extends ModuleTrait {
	import ResultQueryModuleMessage._
	import controllers.common.default_error_handler.f
	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]]) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_provincedata(data) => msg_province_func(data)
		case _ => ???
	}
	
	def msg_province_func(data : JsValue)(implicit error_handler : Int => JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
	    import java.text.SimpleDateFormat
	    val fm = new SimpleDateFormat("MM/yyyy")
	    
	    def dateListConditions(getter : JsValue => Any)(key : String, value : JsValue) : Option[DBObject] = getter(value) match {
          case None => None
          case Some(x) => {
              val start = fm.parse(x.asInstanceOf[List[String]].head).getTime
              val end = fm.parse(x.asInstanceOf[List[String]].last).getTime
              Some("Timestamp" $gte start $lte end)
          }
        }
	    
	    def marketListConditions(getter : JsValue => Any)(key : String, value : JsValue) : Option[DBObject] = getter(value) match {
          case None => None
          case Some(x) => {
            val lst = x.asInstanceOf[List[String]].map { str => 
                	key $eq str  
            }
            Some($and(lst))
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
	            //con = conditionsAcc(con, "market" :: Nil, marketListConditions(x => x.asOpt[List[String]]))
	            con
	    }
	    
	    val take = (data \ "take").asOpt[Int].map (x => x).getOrElse(10)
        val skip = (data \ "skip").asOpt[Int].map (x => x).getOrElse(0)
        val order = "Timestamp"
        
	    try {
			conditions match {
              case Nil => 
                  (Some(Map("finalResult" -> toJson((from db() in "FinalResult").selectSkipTop(skip)(take)(order)(resultQueryJsValue(_)).toList))), None)
              case x : List[DBObject] => 
                  (Some(Map("finalResult" -> toJson((from db() in "FinalResult" where $and(x)).selectSkipTop(skip)(take)(order)(resultQueryJsValue(_)).toList))), None)
            }
			
		} catch {
			case ex : Exception => (None, Some(error_handler(ex.getMessage().toInt)))
		}
	}

    ///begin query finalresult	
	def resultQueryJsValue(x : MongoDBObject) : JsValue = {
	    val timeDate = Calendar.getInstance
        timeDate.setTimeInMillis(x.getAs[Number]("Timestamp").get.longValue)
        val createDate = Calendar.getInstance
        createDate.setTimeInMillis(x.getAs[Number]("Createtime").get.longValue)
        var condition = x.getAs[Map[String,List[String]]]("Condition").get
	    
        println(s"hospital = ${condition.values.head}")
	    println(s"miniproduct = ${condition.values.toList(1)}")
	    
        toJson(Map(
            "Year" -> toJson(timeDate.get(Calendar.YEAR)),
            "Month" -> toJson((timeDate.get(Calendar.MONTH))+1),
            //"Condition" -> toJson(""),
            //"Filepath" -> toJson(x.getAs[String]("Filepath").get),
            
//            "Hospital" -> toJson(Hospital)
//            "Miniproduct" -> toJson(condition.values.toList(1).head)
            
            //"Rtype" -> toJson(x.getAs[String]("Rtype").get),
            "Sales" -> toJson(x.getAs[Double]("Sales").get),
            "Units" -> toJson(x.getAs[Double]("Units").get)
            //"Createtime" -> toJson(s"${createDate.get(Calendar.YEAR)}-${(createDate.get(Calendar.MONTH) + 1)}-${createDate.get(Calendar.DAY_OF_MONTH)}")
        ))
	}
	
//	def queryHospitalInfoByPhas(x : List[String]) : JsValue = {
//	    
//	}
//	
//	def queryMiniProductInfoByIds(x : List[String]) : JsValue = {
//	    
//	}
	
	def resultQueryDetailsJsValue(x : MongoDBObject) : JsValue = {
	    val date = Calendar.getInstance
        date.setTimeInMillis(x.getAs[Number]("Timestamp").get.longValue)
        toJson(Map(
                "year" -> toJson(date.get(Calendar.YEAR)),
                "month" -> toJson(date.get(Calendar.MONTH)),
                "Region_Name" -> toJson(x.getAs[String]("Region_Name").get),
                "Province_Name" -> toJson(x.getAs[String]("Province_Name").get),
                "City_Name" -> toJson(x.getAs[String]("City_Name").get),
                "City_Tier" -> toJson(x.getAs[String]("City_Tier").get),
                "Hosp_Name" -> toJson(x.getAs[String]("Hosp_Name").get),
                "Tag" -> toJson(x.getAs[String]("Tag").get),
                "miniproductName_Ch" -> toJson(x.getAs[String]("miniproductName_Ch").get),
                "miniproductName_En" -> toJson(x.getAs[String]("miniproductName_En").get),
                "Manufacturer_Ch" -> toJson(x.getAs[String]("Manufacturer_Ch").get),
                "Manufacturer_En" -> toJson(x.getAs[String]("Manufacturer_En").get),
                "Drug_Ch" -> toJson(x.getAs[String]("Drug_Ch").get),
                "Drug_En" -> toJson(x.getAs[String]("Drug_En").get),
                "Products_Ch" -> toJson(x.getAs[String]("Products_Ch").get),
                "Products_En" -> toJson(x.getAs[String]("Products_En").get),
                "DosageForm_Ch" -> toJson(x.getAs[String]("DosageForm_Ch").get),
                "DosageForm_En" -> toJson(x.getAs[String]("DosageForm_En").get),
                "DrugSpecification_Ch" -> toJson(x.getAs[String]("DrugSpecification_Ch").get),
                "DrugSpecification_En" -> toJson(x.getAs[String]("DrugSpecification_En").get),
                "Package_Quantity_Ch" -> toJson(x.getAs[String]("Package_Quantity_Ch").get),
                "Package_Quantity_En" -> toJson(x.getAs[String]("Package_Quantity_En").get),
                "sku_Ch" -> toJson(x.getAs[String]("sku_Ch").get),
                "sku_En" -> toJson(x.getAs[String]("sku_En").get),
                "Market1_Code_Ch" -> toJson(x.getAs[String]("Market1_Code_Ch").get),
                "Market1_Code_En" -> toJson(x.getAs[String]("Market1_Code_En").get),
                "Market2_Code_Ch" -> toJson(x.getAs[String]("Market2_Code_Ch").get),
                "Market2_Code_En" -> toJson(x.getAs[String]("Market2_Code_En").get),
                "Market3_Code_Ch" -> toJson(x.getAs[String]("Market3_Code_Ch").get),
                "Market3_Code_En" -> toJson(x.getAs[String]("Market3_Code_En").get),
                "Sales" -> toJson(x.getAs[String]("Sales").get),
                "Units" -> toJson(x.getAs[String]("Units").get)
                ))
	}
}