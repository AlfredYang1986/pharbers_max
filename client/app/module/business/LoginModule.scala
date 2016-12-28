package module.business

import com.pharbers.aqll.pattern.CommonMessage
import play.api.libs.json._
import play.api.libs.json.Json._
import com.pharbers.aqll.pattern.ModuleTrait
import com.pharbers.aqll.pattern.MessageDefines
import com.pharbers.aqll.util.dao.from
import com.mongodb.DBObject
import com.mongodb.casbah.Imports._
import com.pharbers.aqll.util.dao._data_connection_basic
import com.pharbers.aqll.util.MD5

object LoginModuleMessage {
    sealed class msg_LoginBaseQuery extends CommonMessage
    case class msg_login(data: JsValue) extends msg_LoginBaseQuery
}

object LoginModule extends ModuleTrait {
    import LoginModuleMessage._
    import controllers.common.default_error_handler.f
    def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]]): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_login(data) => login(data)
        case _ => println("Error--------"); ???
    }

    def login(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        
        def userConditions(getter : JsValue => Option[Any])(key : String, value : JsValue) : Option[DBObject] = getter(value) match {
          case None => None
          case Some(x) =>
              if(x.asInstanceOf[String].equals("")) {
                  None
              }else {
                  Some(key $eq MD5.md5(x.asInstanceOf[String]))
              }
              
        }

        def conditionsAcc(o: List[DBObject], keys: List[String], func: (String, JsValue) => Option[DBObject]): List[DBObject] = keys match {
            case Nil => o
            case head :: lst => func(head, (data \ head)) match {
                case None => conditionsAcc(o, lst, func)
                case Some(y) => conditionsAcc(y :: o, lst, func)
            }
        }

        def conditions: List[DBObject] = {
            var con = conditionsAcc(Nil, "ID" :: "Password" :: Nil, userConditions(x => x.asOpt[String]))
            con
        }

        try {
            conditions.size match {
                case 0 => (Some(Map("FinalResult" -> toJson("input is null"))), None)
                
                case 1 => (Some(Map("FinalResult" -> toJson("input is null"))), None)
                
                case 2 => conditions match {
                    case x: List[DBObject] => 
                        val tmp = (from db () in "Company" where $and(x)).select(resultData(_))(_data_connection_basic).toList
                        tmp.size match {
                            case 0 => (Some(Map("FinalResult" -> toJson("is null"))), None)
                            case _ => (Some(Map("FinalResult" -> tmp.head)),None)
                        }
                }
                case _ => ???
            }
        } catch {
            case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
        }
    }

    def resultData(x: MongoDBObject): JsValue = {
        val Name = x.getAs[String]("Name").get
        val Token = x.getAs[String]("Token").get
        val Timestamp = x.getAs[Number]("Timestamp").get.longValue()
        toJson(Map("Name" -> toJson(Name),
            "Token" -> toJson(Token),
            "Timestamp" -> toJson(Timestamp)))
    }
}