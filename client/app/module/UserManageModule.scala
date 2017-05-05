package module

import com.mongodb.casbah.commons.{MongoDBList, MongoDBObject}
import com.pharbers.aqll.pattern.{CommonMessage, MessageDefines, ModuleTrait}
import com.mongodb.{BasicDBList,DBObject}
import com.pharbers.aqll.util.dao._data_connection_basic
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.pharbers.aqll.util.{DateUtils, MD5}
import module.common.alMessage._

object UserManageModuleMessage {
    sealed class msg_UserManageBase extends CommonMessage
    case class msg_usermanage_query(data: JsValue) extends msg_UserManageBase
    case class msg_usermanage_delete(data: JsValue) extends msg_UserManageBase
    case class msg_usermanage_findOne(data: JsValue) extends msg_UserManageBase
    case class msg_usermanage_save(data: JsValue) extends msg_UserManageBase
}

object UserManageModule extends ModuleTrait {
    import UserManageModuleMessage._
    import controllers.common.default_error_handler.f
    def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]]): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_usermanage_query(data) => query_func(data)
        case msg_usermanage_delete(data) => delete_func(data)
        case msg_usermanage_findOne(data) => findOne_func(data)
        case msg_usermanage_save(data) => save_func(data)
    }

    def query_func(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            (Some(Map("result" -> toJson(query(data)))),None)
        } catch {
            case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
        }
    }

    def delete_func(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            (Some(Map("result" -> toJson("ok"))),None)
        } catch {
            case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
        }
    }

    def findOne_func(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            val User_Id = (data \ "User_Id").get.asOpt[String].getOrElse("")
            val Company_Id = (data \ "Company_Id").get.asOpt[String].getOrElse("")
            val user = findOneByCompany(Company_Id).find(user => (user \ "User_ID").get.asOpt[String].get.equals(User_Id))
            val result = user match {
                case None => getMessage(4)
                case _ => toJson(Map("result" -> user.get,"status" -> toJson("success")))
            }
            (Some(Map("result" -> result)),None)
        } catch {
            case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
        }
    }

    def save_func(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            val au = (data \ "au").get.asOpt[String].getOrElse("")
            val User_ID = (data \ "User_ID").get.asOpt[String].getOrElse("")
            val Company_Id = (data \ "Company_Id").get.asOpt[String].getOrElse("")
            val user = alMongoDBObject(data)
            val companys = findOneByCompany(Company_Id)
            au match {
                case "add" => {
                    val users = companys.find(user => (user \ "User_ID").get.asOpt[String].get.equals(User_ID)) match {
                        case None => {
                            (user :: Nil).++:(companys.map{x =>
                                MongoDBObject(
                                    "ID" -> (x \ "User_ID").get.asOpt[String].getOrElse(""),
                                    "Account" -> (x \ "Account").get.asOpt[String].getOrElse(""),
                                    "Name" -> (x \ "Name").get.asOpt[String].getOrElse(""),
                                    "Password" -> (x \ "Password").get.asOpt[String].getOrElse(""),
                                    "auth" -> (x \ "auth").get.asOpt[Int].getOrElse(0),
                                    "isadministrator" -> (x \ "isadministrator1").get.asOpt[Int].getOrElse(0),
                                    "Timestamp" -> (x \ "CreateDate").get.asOpt[Long].getOrElse("")
                                )
                            })
                        }
                        case _ => Nil
                    }
                    val result = users match {
                        case Nil => getMessage(3)
                        case _ => {
                            val map = MongoDBObject(
                                "Company_Id" -> (companys.head \ "Company_Id").get.asOpt[String],
                                "Company_Name" -> MongoDBList(MongoDBObject(
                                    "Ch" -> (companys.head \ "Company_Name_Ch").get.asOpt[String],
                                    "En" -> (companys.head \ "Company_Name_En").get.asOpt[String]
                                )),
                                "E-Mail" -> (companys.head \ "E_Mail").get.asOpt[String],
                                "Timestamp" -> System.currentTimeMillis(),
                                "User_lst" -> users
                            )
                            _data_connection_basic.getCollection("Company").findAndRemove(MongoDBObject("Company_Id" -> Company_Id))
                            val r = _data_connection_basic.getCollection("Company").insert(map)
                            r.getN match {
                                case 0 => getMessage(1)
                                case _ => getMessage(2)
                            }
                        }
                    }
                    (Some(Map("result" -> result)),None)
                }
                case "update" => {
                    val users = companys.map{x =>
                        (x \ "User_ID").get.asOpt[String].getOrElse("") match {
                            case i if i.equals(User_ID) => user
                            case _ => MongoDBObject(
                                "ID" -> (x \ "User_ID").get.asOpt[String].getOrElse(""),
                                "Account" -> (x \ "Account").get.asOpt[String].getOrElse(""),
                                "Name" -> (x \ "Name").get.asOpt[String].getOrElse(""),
                                "Password" -> (x \ "Password").get.asOpt[String].getOrElse(""),
                                "auth" -> (x \ "auth").get.asOpt[Int].getOrElse(0),
                                "isadministrator" -> (x \ "isadministrator1").get.asOpt[Int].getOrElse(0),
                                "Timestamp" -> (x \ "CreateDate").get.asOpt[Long].getOrElse("")
                            )
                        }

                    }
                    val map = MongoDBObject(
                        "Company_Id" -> (companys.head \ "Company_Id").get.asOpt[String],
                        "Company_Name" -> MongoDBList(MongoDBObject(
                            "Ch" -> (companys.head \ "Company_Name_Ch").get.asOpt[String],
                            "En" -> (companys.head \ "Company_Name_En").get.asOpt[String]
                        )),
                        "E-Mail" -> (companys.head \ "E_Mail").get.asOpt[String],
                        "Timestamp" -> System.currentTimeMillis(),
                        "User_lst" -> users)
                    val query = MongoDBObject("Company_Id" ->Company_Id)
                    val r = _data_connection_basic.getCollection("Company").update(query,map)
                    val result = r.getN match {
                        case 1 => getMessage(1)
                        case _ => getMessage(2)
                    }
                    (Some(Map("result" -> result)),None)
                }
            }
        } catch {
            case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
        }
    }

    def query(data: JsValue) : List[JsValue] = {
        val Company_Id = (data \ "company").get.asOpt[String].getOrElse("")
        findOneByCompany(Company_Id)
    }

    def alMongoDBObject(data: JsValue): DBObject ={
        val au = (data \ "au").get.asOpt[String].getOrElse("")
        val User_ID = (data \ "User_ID").get.asOpt[String].getOrElse("")
        val Account = (data \ "Account").get.asOpt[String].getOrElse("")
        val password = (data \ "Password").get.asOpt[String].getOrElse("")
        MongoDBObject(
            "ID" -> (if(au.equals("update")){User_ID}else{MD5.md5(Account)}),
            "Account" -> (data \ "Account").get.asOpt[String].getOrElse(""),
            "Name" -> (data \ "User_Name").get.asOpt[String].getOrElse(""),
            "Password" -> (if(au.equals("update")){password}else{MD5.md5(password)}),
            "auth" -> (data \ "auth").get.asOpt[Int].getOrElse(0),
            "isadministrator" -> (data \ "isadministrator").get.asOpt[Int].getOrElse(0),
            "Timestamp" -> System.currentTimeMillis()
        )
    }


    def findOneByCompany(companyid: String): List[JsValue] ={
        val query = MongoDBObject("Company_Id" -> companyid)
        val lst = _data_connection_basic.getCollection("Company").find(query).toList
        lst match {
            case Nil => Nil
            case _ => {
                val r = lst.head
                val Company_Id = r.get("Company_Id").asInstanceOf[String]
                val E_Mail = r.get("E-Mail").asInstanceOf[String]
                val Company_Name = r.get("Company_Name").asInstanceOf[BasicDBList].toArray.head.asInstanceOf[DBObject]
                val Ch = Company_Name.get("Ch").asInstanceOf[String]
                val En = Company_Name.get("En").asInstanceOf[String]
                val User_lst = r.get("User_lst").asInstanceOf[BasicDBList].toArray
                User_lst.map{x =>
                    val user = x.asInstanceOf[DBObject]
                    toJson(Map(
                        "User_ID" -> toJson(user.get("ID").asInstanceOf[String]),
                        "Account" -> toJson(user.get("Account").asInstanceOf[String]),
                        "Name" -> toJson(user.get("Name").asInstanceOf[String]),
                        "Password" -> toJson(user.get("Password").asInstanceOf[String]),
                        "auth" -> toJson(user.get("auth").asInstanceOf[Number].intValue()),
                        "isadministrator" -> toJson(user.get("isadministrator").asInstanceOf[Number].intValue() match {
                            case 0 => "普通用户"
                            case _ => "管理员"
                        }),
                        "isadministrator1" -> toJson(user.get("isadministrator").asInstanceOf[Number].intValue()),
                        "Company_Id" -> toJson(Company_Id),
                        "E_Mail" -> toJson(E_Mail),
                        "Company_Name_Ch" -> toJson(Ch),
                        "Company_Name_En" -> toJson(En),
                        "Timestamp" -> toJson(DateUtils.Timestamp2yyyyMMdd(user.get("Timestamp").asInstanceOf[Number].longValue())),
                        "CreateDate" -> toJson(user.get("Timestamp").asInstanceOf[Number].longValue())
                    ))
                } toList
            }
        }
    }
}