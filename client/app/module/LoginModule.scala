package module

import com.mongodb.DBObject
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.pattern.{CommonMessage, MessageDefines, ModuleTrait}
import com.pharbers.aqll.common.alEncryption.alEncryptionOpt
import com.pharbers.aqll.common.alDao.{_data_connection_basic, from}
import play.api.libs.json.Json._
import play.api.libs.json._

object LoginModuleMessage {
    sealed class msg_LoginBaseQuery extends CommonMessage
    case class msg_login(data: JsValue, ip: String) extends msg_LoginBaseQuery
}

object LoginModule extends ModuleTrait {
    import LoginModuleMessage._
    import controllers.common.default_error_handler.f
    def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]]): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_login(data, ip) => login(data, ip)
        case _ => println("Error--------"); ???
    }

    def login(data: JsValue, ip: String)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        def userConditions(getter : JsValue => Option[Any])(key : String, value : JsValue) : Option[DBObject] = getter(value) match {
          case None => None
          case Some(x) =>
              if(x.asInstanceOf[String].equals("")) {
                  None
              }else {
                  if(key.equals("Account")) {
                      Some("User_lst."+key $eq x.toString)
                  }else {
                      Some("User_lst."+key $eq alEncryptionOpt.md5(x.asInstanceOf[String]))
                  }
              }

        }

        def conditionsAcc(o: List[DBObject], keys: List[String], func: (String, JsValue) => Option[DBObject]): List[DBObject] = keys match {
            case Nil => o
            case head :: lst => func(head, (data \ head).as[JsValue]) match {
                case None => conditionsAcc(o, lst, func)
                case Some(y) => conditionsAcc(y :: o, lst, func)
            }
        }

        def conditions: List[DBObject] = {
            val con = conditionsAcc(Nil, "Account" :: "Password" :: Nil, userConditions(x => x.asOpt[String]))
            con
        }

        try {
            conditions.size match {
                case 0 => (Some(Map("FinalResult" -> toJson("input is null"))), None)

                case 1 => (Some(Map("FinalResult" -> toJson("input is null"))), None)

                case 2 => conditions match {
                    case x: List[DBObject] =>
                        val t: List[DBObject] = List(("$unwind" $eq "$User_lst"), ("$match" $eq (x(0) ++ x(1))))
                        val tmp = (from db () in "Company" where t).selectAggregate(resultData(_, ip))(_data_connection_basic).toList
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

    def resultData(x: MongoDBObject, ip: String): JsValue = {
        val User_lst = x.getAs[MongoDBObject]("User_lst").get
        val Company = x.getAs[MongoDBList]("Company_Name").get
        val Company_Id = x.getAs[String]("Company_Id").get
        val Timestamp = x.getAs[Number]("Timestamp").get.longValue()
        val E_Mail = x.getAs[String]("E-Mail").get
        val UserName = User_lst.getAs[String]("Name").getOrElse("无")
        val UserId = User_lst.getAs[String]("ID").getOrElse("无")
        val UserTimestamp = User_lst.as[Number]("Timestamp").longValue()
        val UserAuth = User_lst.as[Number]("auth").intValue()
        val IsAdministrator = User_lst.as[Number]("isadministrator").intValue()
        val CompanyNameCh = Company.head.asInstanceOf[BasicDBObject].get("Ch").toString
        val CompanyNameEn = Company.head.asInstanceOf[BasicDBObject].get("En").toString

        toJson(Map("UserName" -> toJson(UserName),
            "Token" -> toJson(Company_Id),
            "E_Mail" -> toJson(E_Mail),
            "UserTimestamp" -> toJson(UserTimestamp),
            "UserAuth" -> toJson(UserAuth),
            "IsAdministrator" -> toJson(IsAdministrator),
            "User_Token" -> toJson(UserId),
            "CompanyNameCh" -> toJson(CompanyNameCh),
            "CompanyNameEn" -> toJson(CompanyNameEn),
            "ip" -> toJson(ip),
            "Timestamp" -> toJson(Timestamp)))
    }
}