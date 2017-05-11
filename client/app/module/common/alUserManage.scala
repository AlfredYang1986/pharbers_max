package module.common

import com.mongodb.{BasicDBList, BasicDBObject, DBObject}
import com.mongodb.casbah.commons.{MongoDBList, MongoDBObject}
import com.pharbers.aqll.util.{DateUtils, MD5}
import com.pharbers.aqll.util.dao._data_connection_basic
import module.common.alMessage.getMessage
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

/**
  * Created by liwei on 2017/5/10.
  */
object alUserManage {
  /**
    * 查询用户
    *
    * @author liwei
    * @param data
    * @return
    */
  def query(data: JsValue): JsValue ={
    val Company_Id = (data \ "Company_Id").get.asOpt[String].get
    val result = Company_Id match {
      case i if i.equals("788d4ff5836bcee2ebf4940fec882ac8") => _data_connection_basic.getCollection("Company").find().toList.map(x => queryDBObject(x: DBObject))
      case _ => {
        val query = MongoDBObject("Company_Id" -> Company_Id)
        _data_connection_basic.getCollection("Company").find(query).toList.map(x => queryDBObject(x: DBObject))
      }
    }
    toJson(result)
  }

  /**
    * 查询用户分页数据
    *
    * @author liwei
    * @param x
    * @return
    */
  def queryDBObject(x: DBObject): JsValue ={
    val Company_Id = x.get("Company_Id").asInstanceOf[String]
    val User_lst = x.get("User_lst").asInstanceOf[BasicDBList].toArray.map { y =>
      val user = y.asInstanceOf[DBObject]
      toJson(Map(
        "Company_Id" -> toJson(Company_Id),
        "ID" -> toJson(user.get("ID").asInstanceOf[String]),
        "Account" -> toJson(user.get("Account").asInstanceOf[String]),
        "Name" -> toJson(user.get("Name").asInstanceOf[String]),
        "Password" -> toJson(user.get("Password").asInstanceOf[String]),
        "auth" -> toJson(user.get("auth").asInstanceOf[Number].longValue()),
        "isadministrator" -> toJson(user.get("isadministrator").asInstanceOf[Number].longValue() match {
          case 0 => "普通用户"
          case 1 => "管理员"}),
        "Timestamp" -> toJson(DateUtils.Timestamp2yyyyMMdd(user.get("Timestamp").asInstanceOf[Number].longValue()))
      ))
    }
    toJson(User_lst)
  }

  /**
    * 删除用户数据
    *
    * @author liwei
    * @param data
    * @return
    */
  def delete(data: JsValue): JsValue ={
    val Company_Id = (data \ "Company_Id").get.asOpt[String].getOrElse("")
    val IDs = (data \ "IDs").get.asOpt[List[String]].getOrElse(Nil)
    val companys = findOneByCompany(Company_Id)
    val com_head = companys.head
    val query = MongoDBObject("Company_Id" -> Company_Id)
    val document: BasicDBObject = new BasicDBObject()
    document.put("Company_Id",com_head.get("Company_Id").get.asInstanceOf[String])
    val sub_document: BasicDBObject = new BasicDBObject()
    sub_document.put("Ch",com_head.get("Company_Name_Ch").get.asInstanceOf[String])
    sub_document.put("En",com_head.get("Company_Name_En").get.asInstanceOf[String])
    val sub_list: BasicDBList = new BasicDBList()
    sub_list.add(sub_document)
    document.put("Company_Name",sub_list)
    document.put("E-Mail",com_head.get("E_Mail").get.asInstanceOf[String])
    document.put("Timestamp",com_head.get("Timestamp").get.asInstanceOf[AnyRef])
    val sub_user_list: BasicDBList = new BasicDBList()
    companys.map { x =>
      if (!x.get("User_ID").get.asInstanceOf[String].equals(IDs.head)) {
        val sub_user_obj: BasicDBObject = new BasicDBObject()
        sub_user_obj.put("ID", x.get("User_ID").get.asInstanceOf[String])
        sub_user_obj.put("Account", x.get("Account").get.asInstanceOf[String])
        sub_user_obj.put("Name", x.get("Name").get.asInstanceOf[String])
        sub_user_obj.put("Password", x.get("Password").get.asInstanceOf[String])
        sub_user_obj.put("auth", x.get("auth").get.asInstanceOf[AnyRef])
        sub_user_obj.put("isadministrator", x.get("isadministrator").get.asInstanceOf[AnyRef])
        sub_user_obj.put("Timestamp", x.get("Timestamp").get.asInstanceOf[AnyRef])
        sub_user_list.add(sub_user_obj)
      }
    }
    document.put("User_lst",sub_user_list)
    val del_result = _data_connection_basic.getCollection("Company").findAndRemove(query)
    del_result match {
      case None => getMessage(2)
      case _ => {
        _data_connection_basic.getCollection("Company").insert(document) getN match {
          case 0 => getMessage(1)
          case _ => getMessage(2)
        }
      }
    }
  }

  /**
    * 查询单个用户数据
    *
    * @author liwei
    * @param data
    * @return
    */
  def findOne(data: JsValue): JsValue ={
    val ID = (data \ "ID").get.asOpt[String].get
    var lsb = toJson("")
    _data_connection_basic.getCollection("Company").find().toList.foreach{x =>
      val users = x.get("User_lst").asInstanceOf[BasicDBList].toArray
      users.foreach{y =>
        val user = y.asInstanceOf[DBObject]
        if(ID.equals(user.get("ID").asInstanceOf[String])){
          lsb = toJson(Map("result" -> toJson(Map(
            //"Companys" -> (if(x.get("Company_Id").asInstanceOf[String].equals("788d4ff5836bcee2ebf4940fec882ac8")){alCompany.query(toJson(Map("Company_Id" -> toJson("788d4ff5836bcee2ebf4940fec882ac8"))))}),
            //"Company_Id" -> toJson(x.get("Company_Id").asInstanceOf[String]),
            "ID" -> toJson(user.get("ID").asInstanceOf[String]),
            "Account" -> toJson(user.get("Account").asInstanceOf[String]),
            "Name" -> toJson(user.get("Name").asInstanceOf[String]),
            "Password" -> toJson(user.get("Password").asInstanceOf[String]),
            "auth" -> toJson(user.get("auth").asInstanceOf[Number].intValue()),
            "isadministrator" -> toJson(user.get("isadministrator").asInstanceOf[Number].intValue()),
            "Timestamp" -> toJson(DateUtils.Timestamp2yyyyMMdd(user.get("Timestamp").asInstanceOf[Number].longValue()))
          )),"status" -> toJson("success")))
        }
      }
    }
    lsb
  }

  /**
    * 保存/修改用户数据
    *
    * @author liwei
    * @param data
    * @return
    */
  def save(data: JsValue): JsValue ={
    val au = (data \ "au").get.asOpt[String].getOrElse("")
    val Account = (data \ "Account").get.asOpt[String].getOrElse("")
    val password = (data \ "Password").get.asOpt[String].getOrElse("")
    val Company_Id = (data \ "Company_Id").get.asOpt[String].getOrElse("")
    val ID = au match {
        case i if i.equals("update") => (data \ "ID").get.asOpt[String].getOrElse("")
        case _ => MD5.md5(Account)
    }
    val isadmin = (data \ "isadmin").get.asOpt[Int].getOrElse(0)
    //println(s"au=$au Account=$Account password=$password Company_Id=$Company_Id ID=$ID isadmin=$isadmin")
    val user = MongoDBObject(
        "ID" -> ID,
        "Account" -> Account,
        "Name" -> (data \ "Name").get.asOpt[String].getOrElse(""),
        "Password" -> (if(au.equals("update")){password}else{MD5.md5(password)}),
        "auth" -> 0,
        "isadministrator" -> isadmin,
        "Timestamp" -> System.currentTimeMillis()
    )
    val companys = findOneByCompany(Company_Id)
    companys match {
      case Nil => getMessage(0)
      case _ => {
        val com_head = companys.head
        au match {
          case "add" => {
            companys.find(x => x.get("User_ID").get.asInstanceOf[String].equals(ID)) match {
              case None => {
                val query = MongoDBObject("Company_Id" -> Company_Id)
                _data_connection_basic.getCollection("Company").findAndRemove(query)
                val doc = MongoDBObject(
                  "Company_Id" -> com_head.get("Company_Id").get.asInstanceOf[String],
                  "Company_Name" -> MongoDBList(MongoDBObject(
                    "Ch" -> com_head.get("Company_Name_Ch").get.asInstanceOf[String],
                    "En" -> com_head.get("Company_Name_En").get.asInstanceOf[String]
                  )),
                  "E-Mail" -> com_head.get("E_Mail").get.asInstanceOf[String],
                  "Timestamp" -> com_head.get("Timestamp").get.asInstanceOf[Number].longValue(),
                  "User_lst" -> (user :: Nil).++:(companys.map{x =>
                    MongoDBObject(
                      "ID" -> x.get("User_ID").get.asInstanceOf[String],
                      "Account" -> x.get("Account").get.asInstanceOf[String],
                      "Name" -> x.get("Name").get.asInstanceOf[String],
                      "Password" -> x.get("Password").get.asInstanceOf[String],
                      "auth" -> x.get("auth").get.asInstanceOf[Number].intValue(),
                      "isadministrator" -> x.get("isadministrator").get.asInstanceOf[Number].intValue(),
                      "Timestamp" -> x.get("Timestamp").get.asInstanceOf[Number].longValue()
                    )
                  }))
                _data_connection_basic.getCollection("Company").insert(doc) getN match {
                  case 0 => getMessage(1)
                  case _ => getMessage(2)
                }
              }
              case _ => getMessage(3)
            }
          }
          case "update" => {
            val query = MongoDBObject("Company_Id" -> Company_Id)
            val sql = MongoDBObject(
              "Company_Id" -> com_head.get("Company_Id").get.asInstanceOf[String],
              "Company_Name" -> MongoDBList(MongoDBObject(
                "Ch" -> com_head.get("Company_Name_Ch").get.asInstanceOf[String],
                "En" -> com_head.get("Company_Name_En").get.asInstanceOf[String]
              )),
              "E-Mail" -> com_head.get("E_Mail").get.asInstanceOf[String],
              "Timestamp" -> com_head.get("Timestamp").get.asInstanceOf[Number].longValue(),
              "User_lst" -> companys.map{x =>
                x.get("User_ID").get.asInstanceOf[String] match {
                  case i if i.equals(ID) => user
                  case _ => MongoDBObject(
                    "ID" -> x.get("User_ID").get.asInstanceOf[String],
                    "Account" -> x.get("Account").get.asInstanceOf[String],
                    "Name" -> x.get("Name").get.asInstanceOf[String],
                    "Password" -> x.get("Password").get.asInstanceOf[String],
                    "auth" -> x.get("auth").get.asInstanceOf[Number].intValue(),
                    "isadministrator" -> x.get("isadministrator").get.asInstanceOf[Number].intValue(),
                    "Timestamp" -> x.get("Timestamp").get.asInstanceOf[Number].longValue()
                  )
                }

              })
            _data_connection_basic.getCollection("Company").update(query,sql).getN match {
              case 1 => getMessage(1)
              case _ => getMessage(2)
            }
          }
        }
      }
    }
  }

  /**
    * 根据公司查询数据
    *
    * @author liwei
    * @param companyid
    * @return
    */
  def findOneByCompany(companyid: String): List[Map[String,Any]] ={
      val lst = _data_connection_basic.getCollection("Company").find(MongoDBObject("Company_Id" -> companyid)).toList
      lst match {
          case Nil => Nil
          case _ => {
              val r = lst.head
              val Company_Name = r.get("Company_Name").asInstanceOf[BasicDBList].toArray.head.asInstanceOf[DBObject]
              val User_lst = r.get("User_lst").asInstanceOf[BasicDBList].toArray
              User_lst.map{x =>
                  val user = x.asInstanceOf[DBObject]
                  Map(
                      "User_ID" -> user.get("ID").asInstanceOf[String],
                      "Account" -> user.get("Account").asInstanceOf[String],
                      "Name" -> user.get("Name").asInstanceOf[String],
                      "Password" -> user.get("Password").asInstanceOf[String],
                      "auth" -> user.get("auth").asInstanceOf[Number].intValue(),
                      "isadministrator" -> user.get("isadministrator").asInstanceOf[Number].intValue(),
                      "Company_Id" -> r.get("Company_Id").asInstanceOf[String],
                      "E_Mail" -> r.get("E-Mail").asInstanceOf[String],
                      "Company_Name_Ch" -> Company_Name.get("Ch").asInstanceOf[String],
                      "Company_Name_En" -> Company_Name.get("En").asInstanceOf[String],
                      "Timestamp" -> user.get("Timestamp").asInstanceOf[Number].longValue()
                  )
              } toList
          }
      }
  }
}
