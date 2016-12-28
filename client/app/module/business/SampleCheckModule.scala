package module.business

import play.api.libs.json._
import play.api.libs.json.Json.toJson
import com.pharbers.aqll.pattern.CommonMessage
import com.pharbers.aqll.pattern.ModuleTrait
import com.pharbers.aqll.pattern.MessageDefines
import com.pharbers.aqll.util.dao.from
import com.mongodb.DBObject
import com.mongodb.casbah.Imports._
import com.pharbers.aqll.util.dao._data_connection_cores


object SampleCheckModuleMessage {
    sealed class msg_CheckBaseQuery extends CommonMessage 
    case class msg_samplecheck(data: JsValue) extends msg_CheckBaseQuery
}

object SampleCheckModule extends ModuleTrait {
    import SampleCheckModuleMessage._
    import controllers.common.default_error_handler.f
    
    def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]]) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_samplecheck(data) => check(data)
        case _ => println("Error---------------");???
    }
    
    def check(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val company = (data \ "company").asOpt[String].get
        val take = (data \ "take").asOpt[Int].map (x => x).getOrElse(10)
        val skip = (data \ "skip").asOpt[Int].map (x => x).getOrElse(0)

        try {
            val conditions = ("ID" -> "21c890ca4b56fd5e61a56010fe96d660")
            val d = (from db() in "FactResult" where $and(conditions)).select(resultData(_))(_data_connection_cores)
			(Some(d.head), None)
		} catch {
			case ex : Exception => (None, Some(error_handler(ex.getMessage().toInt)))
		}
    }
    
    def resultData(d: MongoDBObject): Map[String, JsValue] = {
        val t = d.getAs[MongoDBObject]("Condition").get
        val hospNum = d.getAs[Number]("HospitalNum").get.longValue
        val miniProNum = d.getAs[Number]("ProductMinuntNum").get.intValue
        val sales = d.getAs[Number]("Sales").get.doubleValue
        val hospList = t.getAs[MongoDBList]("Hospital").get.toList.asInstanceOf[List[String]]

//        val miniPorList = t.getAs[MongoDBList]("ProductMinunt").toList.asInstanceOf[List[String]]
        Map("hospNum" -> toJson(hospNum),
            "miniProNum" -> toJson(miniProNum),
            "sales" -> toJson(sales),
            "hospList" -> toJson(hospList)
            )
    }
}