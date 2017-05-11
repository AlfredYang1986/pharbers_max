package module.common

/**
  * Created by liwei on 2017/5/10.
  */
import com.mongodb.casbah.commons.{MongoDBList, MongoDBObject}
import com.mongodb.{BasicDBList,DBObject}
import com.pharbers.aqll.util.dao._data_connection_basic
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.pharbers.aqll.util.{DateUtils, MD5}
import module.common.alMessage._

object alCompany {

  /**
    * 查询公司数据
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
    * 查询公司分页数据
    *
    * @author liwei
    * @param x
    * @return
    */
  def queryDBObject(x: DBObject): JsValue ={
    val Company_Name_lst = x.get("Company_Name").asInstanceOf[BasicDBList].toArray.head.asInstanceOf[DBObject]
    toJson(Map(
      "Company_Id" -> toJson(x.get("Company_Id").asInstanceOf[String]),
      "Ch" -> toJson(Company_Name_lst.get("Ch").asInstanceOf[String]),
      "En" -> toJson(Company_Name_lst.get("En").asInstanceOf[String]),
      "E_Mail" -> toJson(x.get("E-Mail").asInstanceOf[String]),
      "Timestamp" -> toJson(DateUtils.Timestamp2yyyyMMdd(x.get("Timestamp").asInstanceOf[Number].longValue()))))
  }

  /**
    * 删除公司数据
    *
    * @author liwei
    * @param data
    * @return
    */
  def delete(data: JsValue): JsValue ={
    val ids = (data \ "Company_Id").get.asOpt[List[String]].getOrElse(Nil)
    val r = ids map(x => _data_connection_basic.getCollection("Company").findAndRemove(MongoDBObject("Company_Id" -> x)))
    r.size match {
      case i if i.equals(ids.size) => getMessage(1)
      case _ => getMessage(2)
    }
  }

  /**
    * 查询单个公司数据
    *
    * @author liwei
    * @param data
    * @return
    */
  def findOne(data: JsValue): JsValue ={
    val Company_Id = (data \ "Company_Id").get.asOpt[String].getOrElse("")
    val query =MongoDBObject("Company_Id" -> Company_Id)
    val company = _data_connection_basic.getCollection("Company").findOne(query)
    company match {
      case None => getMessage(4)
      case _ => {
        val Company_Name = company.get.get("Company_Name").asInstanceOf[BasicDBList].toArray.head.asInstanceOf[DBObject]
        val User_lst = company.get.get("User_lst").asInstanceOf[BasicDBList].toArray
        toJson(Map("result" -> toJson(Map(
          "Company_Id" -> toJson(company.get.get("Company_Id").asInstanceOf[String]),
          "Ch" -> toJson(Company_Name.get("Ch").asInstanceOf[String]),
          "En" -> toJson(Company_Name.get("En").asInstanceOf[String]),
          "E_Mail" -> toJson(company.get.get("E-Mail").asInstanceOf[String]),
          "Timestamp" -> toJson(DateUtils.Timestamp2yyyyMMdd(company.get.get("Timestamp").asInstanceOf[Number].longValue())),
          "User_lst" -> toJson(User_lst.size)
        )),"status" -> toJson("success")))
      }
    }
  }

  /**
    * 保存/修改公司数据
    *
    * @author liwei
    * @param data
    * @return
    */
  def save(data: JsValue): JsValue ={
    val au = (data \ "au").get.asOpt[String].getOrElse("")
    val Company_Id = (data \ "Company_Id").get.asOpt[String].getOrElse("")
    val Company_Name_Ch = (data \ "Company_Name_Ch").get.asOpt[String].getOrElse("")
    val Company_Name_En = (data \ "Company_Name_En").get.asOpt[String].getOrElse("")
    val E_Mail = (data \ "E_Mail").get.asOpt[String].getOrElse("")
    au match {
      case "add" => {
        val Company_Id_MD5 = Company_Id match {
          case "" => MD5.md5(Company_Name_Ch match {
            case "" => Company_Name_En
            case _ => Company_Name_Ch
          })
          case _ => Company_Id
        }
        val Company = findOne(toJson(Map("Company_Id" -> toJson(Company_Id_MD5))))
        val status = (Company \ "status").get.asOpt[String].get
        status match {
          case "fail" => {
            _data_connection_basic.getCollection("Company").insert(
              MongoDBObject(
                "Company_Id" -> Company_Id_MD5,
                "Company_Name" -> MongoDBList(MongoDBObject(
                  "Ch" -> (data \ "Company_Name_Ch").get.asOpt[String],
                  "En" -> (data \ "Company_Name_En").get.asOpt[String]
                )),
                "E-Mail" -> (data \ "E_Mail").get.asOpt[String],
                "Timestamp" -> System.currentTimeMillis(),
                "User_lst" -> MongoDBList()
              )
            ) getN match {
                case 0 => getMessage(1)
                case _ => getMessage(2)
            }
          }
          case "success" => getMessage(3)
        }
      }
      case "update" => {
        val query = MongoDBObject("Company_Id" -> Company_Id)
        val company = _data_connection_basic.getCollection("Company").findOne(query)
        _data_connection_basic.getCollection("Company").update(query,
          MongoDBObject(
            "Company_Id" -> Company_Id,
            "Company_Name" -> MongoDBList(MongoDBObject(
              "Ch" -> (data \ "Company_Name_Ch").get.asOpt[String],
              "En" -> (data \ "Company_Name_En").get.asOpt[String]
            )),
            "E-Mail" -> (data \ "E_Mail").get.asOpt[String],
            "Timestamp" -> System.currentTimeMillis(),
            "User_lst" -> company.get.get("User_lst").asInstanceOf[BasicDBList].toArray
          )
        ).getN match {
            case 1 => getMessage(1)
            case _ => getMessage(2)
        }
      }
    }
  }
}
