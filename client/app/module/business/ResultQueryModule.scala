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
	case class msg_citydata(data : JsValue) extends msg_resultqueryBase
	case class msg_hospitaldata(data : JsValue) extends msg_resultqueryBase
	case class msg_error_emp(data : JsValue) extends msg_resultqueryBase
}

object ResultQueryModule extends ModuleTrait {
	import ResultQueryModuleMessage._
	import controllers.common.default_error_handler.f
	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]]) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_provincedata(data) => msg_province_func(data)
		case msg_citydata(data) => msg_city_func(data)(pr)
		case msg_hospitaldata(data) => msg_hospital_func(data)(pr)
		case msg_error_emp(data) => msg_error_emp(data)
		case _ => ???
	}
	
	def msg_province_func(data : JsValue)(implicit error_handler : Int => JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		//(Some(Map("ceshi1" -> toJson("ok"))), None)
	    (Some(Map("provincelst" -> resultQueryByProvinceTestJsValue())), None)
	    
//	    def conditions : List[DBObject] = {var con  = (Map("Timestamp" -> "") :: Nil)}
//		
//	    val take = (data \ "take").asOpt[Int].map (x => x).getOrElse(10)
//        val skip = (data \ "skip").asOpt[Int].map (x => x).getOrElse(0)
//        val order = "Timestamp"
        
//	    try {
//			conditions match {
//              case Nil => 
//                  (Some(Map("ceshi1" -> toJson((from db() in "products").selectSkipTop(skip)(take)(order)(resultQueryByProvinceJsValue(_)).toList))), None)
//              case x : List[DBObject] => 
//                  (Some(Map("ceshi1" -> toJson((from db() in "products" where $and(x)).selectSkipTop(skip)(take)(order)(resultQueryByProvinceJsValue(_)).toList))), None)
//            }
//			
//		} catch {
//			case ex : Exception => (None, Some(error_handler(ex.getMessage().toInt)))
//		}
	}
	
	def msg_city_func(data : JsValue)(pr : Option[Map[String, JsValue]])(implicit error_handler : Int => JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
		    val re = pr match {
				case Some(p) => p + ("citydata" -> resultQueryByCityTestJsValue())
				case None => Map("citydata" -> resultQueryByCityTestJsValue())
			}
		    (Some(re), None)
//	    def conditions : List[DBObject] = {var con  = (Map("Timestamp" -> "") :: Nil)}
//		
//	    val take = (data \ "take").asOpt[Int].map (x => x).getOrElse(10)
//        val skip = (data \ "skip").asOpt[Int].map (x => x).getOrElse(0)
//        val order = "Timestamp"
        
//	    try {
//			conditions match {
//              case Nil => 
//                  (Some(Map("ceshi1" -> toJson((from db() in "products").selectSkipTop(skip)(take)(order)(resultQueryByProvinceJsValue(_)).toList))), None)
//              case x : List[DBObject] => 
//                  (Some(Map("ceshi1" -> toJson((from db() in "products" where $and(x)).selectSkipTop(skip)(take)(order)(resultQueryByProvinceJsValue(_)).toList))), None)
//            }
//			
//		} catch {
//			case ex : Exception => (None, Some(error_handler(ex.getMessage().toInt)))
//		}
		} catch {
			case ex : Exception => (None, Some(error_handler(ex.getMessage().toInt)))
		}	
	}
	
	def msg_hospital_func(data : JsValue)(pr : Option[Map[String, JsValue]])(implicit error_handler : Int => JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
		    val re = pr match {
				case Some(p) => p + ("hospitaldata" -> resultQueryByHospitalTestJsValue())
				case None => Map("hospitaldata" -> resultQueryByHospitalTestJsValue())
			}
		    (Some(re), None)
//	    def conditions : List[DBObject] = {var con  = (Map("Timestamp" -> "") :: Nil)}
//		
//	    val take = (data \ "take").asOpt[Int].map (x => x).getOrElse(10)
//        val skip = (data \ "skip").asOpt[Int].map (x => x).getOrElse(0)
//        val order = "Timestamp"
        
//	    try {
//			conditions match {
//              case Nil => 
//                  (Some(Map("ceshi1" -> toJson((from db() in "products").selectSkipTop(skip)(take)(order)(resultQueryByProvinceJsValue(_)).toList))), None)
//              case x : List[DBObject] => 
//                  (Some(Map("ceshi1" -> toJson((from db() in "products" where $and(x)).selectSkipTop(skip)(take)(order)(resultQueryByProvinceJsValue(_)).toList))), None)
//            }
//			
//		} catch {
//			case ex : Exception => (None, Some(error_handler(ex.getMessage().toInt)))
//		}
			
		} catch {
			case ex : Exception => (None, Some(error_handler(ex.getMessage().toInt)))
		}	
	}
	
	def msg_error_emp(data : JsValue)(implicit error_handler : Int => JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			throw new Exception(-1.toString)
			(None, None)
			
		} catch {
			case ex : Exception => (None, Some(error_handler(ex.getMessage().toInt)))
		}		
	}
////test start	
	def resultQueryByProvinceTestJsValue() : JsValue = {
	    toJson((Map(
                "year" -> "2016",
                "month" -> "12",
                "Sales" -> "21645788.12",
                "Units" -> "124565"
                ) :: Nil))
	}
	
	def resultQueryByCityTestJsValue() : JsValue = {
	    toJson((Map(
                "year" -> "2016",
                "month" -> "12",
                "Sales" -> "21645788.12",
                "Units" -> "124565"
                ) :: Nil))
	}
	
	def resultQueryByHospitalTestJsValue() : JsValue = {
	    toJson((Map(
                "year" -> "2016",
                "month" -> "12",
                "Region_Name" -> "华北区",
                "Province_Name" -> "北京",
                "miniproductName_Ch" -> "安内真片剂10MG7苏州东瑞制药有限公司",
                "miniproductName_En" -> "",
                "Manufacturer_Ch" -> "苏州东瑞制药有限公司",
                "Manufacturer_En" -> "",
                "Drug_Ch" -> "安内真",
                "Drug_En" -> "",
                "Products_Ch" -> "安内真",
                "Products_En" -> "",
                "DosageForm_Ch" -> "片剂",
                "DosageForm_En" -> "",
                "DrugSpecification_Ch" -> "10MG",
                "DrugSpecification_En" -> "",
                "Package_Quantity_Ch" -> "7",
                "Package_Quantity_En" -> "",
                "sku_Ch" -> "",
                "sku_En" -> "",
                "Market1_Code_Ch" -> "降压药市场",
                "Market1_Code_En" -> "",
                "Market2_Code_Ch" -> "博路定市场",
                "Market2_Code_En" -> "",
                "Market3_Code_Ch" -> "ACEI市场",
                "Market3_Code_En" -> "",
                "Sales" -> "6457887123.88",
                "Units" -> "456454455"
                ) :: Nil))
	}
///test end
///database attribute joint start
	def resultQueryByProvinceJsValue(x : MongoDBObject) : JsValue = {
	    val date = Calendar.getInstance
        date.setTimeInMillis(x.getAs[Number]("Timestamp").get.longValue)
        toJson(Map(
                "year" -> toJson(date.get(Calendar.YEAR)),
                "month" -> toJson(date.get(Calendar.MONTH)),
                "Region_Name" -> toJson(x.getAs[String]("Region_Name").get),
                "Province_Name" -> toJson(x.getAs[String]("Province_Name").get),
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
	
	def resultQueryByCityJsValue(x : MongoDBObject) : JsValue = {
	    val date = Calendar.getInstance
        date.setTimeInMillis(x.getAs[Number]("Timestamp").get.longValue)
        toJson(Map(
                "year" -> toJson(date.get(Calendar.YEAR)),
                "month" -> toJson(date.get(Calendar.MONTH)),
                "Region_Name" -> toJson(x.getAs[String]("Region_Name").get),
                "Province_Name" -> toJson(x.getAs[String]("Province_Name").get),
                "City_Name" -> toJson(x.getAs[String]("City_Name").get),
                "City_Tier" -> toJson(x.getAs[String]("City_Tier").get),
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
		
	def resultQueryByHospitalJsValue(x : MongoDBObject) : JsValue = {
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
///database attribute joint end
}