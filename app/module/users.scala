package module

import com.mongodb
import com.mongodb.casbah
import com.mongodb.casbah.Imports
import com.mongodb.casbah.Imports._
import com.pharbers.bmmessages.{CommonMessage, CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.common.MergeStepResult
import module.UserMessage._
import module.common.pharbersmacro.CURDMacro._
import module.datamodel.basemodel
import org.bson.types.ObjectId
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import module.common.processor
import module.common.processor._
import module.stragety.{impl, one2one}

abstract class msg_UserCommand extends CommonMessage("users", UserModule)

object UserMessage {
    case class msg_pushUser(data: JsValue) extends msg_UserCommand
    case class msg_popUser(data : JsValue) extends msg_UserCommand
    case class msg_queryUser(data : JsValue) extends msg_UserCommand
    case class msg_queryUserMulti(data : JsValue) extends msg_UserCommand

    case class msg_bindUserCompany(data : JsValue) extends msg_UserCommand
    case class msg_unbindUserCompany(data : JsValue) extends msg_UserCommand
    case class msg_expendCompanyInfo(data : JsValue) extends msg_UserCommand
}

class user extends basemodel {
    override val name = "user"
    override def runtimeClass: Class[_] = classOf[user]

    val qc : JsValue => DBObject = { js =>
        val tmp = (js \ "condition" \ "user_id").asOpt[String].get
        DBObject("_id" -> new ObjectId(tmp))
    }

    val anqc: JsValue => _root_.com.mongodb.casbah.Imports.DBObject = { js =>
        val tmp = (js \ "condition" \ "user_id").asOpt[String].get
        DBObject("user_id" -> tmp)
    }

    val qcm : JsValue => DBObject = { js =>
        (js \ "condition" \ "users").asOpt[List[String]].get match {
            case Nil => DBObject("query" -> "none")
            case ll : List[String] => $or(ll map (x => DBObject("_id" -> new ObjectId(x))))
        }
    }

    val ssr : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "user_id" -> toJson(obj.getAs[ObjectId]("_id").get.toString)
        )
    }

    val sr : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "user_id" -> toJson(obj.getAs[ObjectId]("_id").get.toString),
            "screen_name" -> toJson(obj.getAs[String]("screen_name").get),
            "screen_photo" -> toJson(obj.getAs[String]("screen_photo").get)
        )
    }

    val dr : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "user_id" -> toJson(obj.getAs[ObjectId]("_id").get.toString),
            "screen_name" -> toJson(obj.getAs[String]("screen_name").get),
            "screen_photo" -> toJson(obj.getAs[String]("screen_photo").get),
            "email" -> toJson(obj.getAs[String]("email").get),
            "phone" -> toJson(obj.getAs[String]("phone").get)
        )
    }

    val popr : DBObject => Map[String, JsValue] = { _ =>
        Map(
            "pop user" -> toJson("success")
        )
    }

    val d2m : JsValue => DBObject = { js =>
        val data = (js \ "user").asOpt[JsValue].map (x => x).getOrElse(toJson(""))

        val builder = MongoDBObject.newBuilder
        builder += "_id" -> ObjectId.get()      // user_id 唯一标示
        builder += "screen_name" -> (data \ "screen_name").asOpt[String].map (x => x).getOrElse("")
        builder += "screen_photo" -> (data \ "screen_photo").asOpt[String].map (x => x).getOrElse("")
        builder += "email" -> (data \ "email").asOpt[String].map (x => x).getOrElse("")     // 登录的唯一标示
        builder += "phone" -> (data \ "phone").asOpt[String].map (x => x).getOrElse("")

        builder.result
    }

    val up2m : (DBObject, JsValue) => DBObject = { (obj, js) =>
        val data = (js \ "user").asOpt[JsValue].get

        (data \ "screen_name").asOpt[String].map (x => obj += "screen_name" -> x).getOrElse(Unit)
        (data \ "screen_photo").asOpt[String].map (x => obj += "screen_photo" -> x).getOrElse(Unit)
        (data \ "email").asOpt[String].map (x => obj += "email" -> x).getOrElse(Unit)
        (data \ "phone").asOpt[String].map (x => obj += "phone" -> x).getOrElse(Unit)

        obj
    }
}

class user2company extends one2one[user, company] {

    override def createThis: user = impl[user]
    override def createThat: company = impl[company]

    override def one2onessr(obj: DBObject): Map[String, JsValue] =
        Map(
            "condition" -> toJson(Map(
                "company_id" -> toJson(obj.getAs[String]("company_id").get)
            ))
        )

    override def unbind(data: JsValue): casbah.Imports.DBObject = {
        val builder = MongoDBObject.newBuilder
        val _id = (data \ "condition" \ "bind_id").asOpt[String].get
        builder += "_id" -> new ObjectId(_id)

        builder.result
    }

    override def bind(data: JsValue): Imports.DBObject = {
        val builder = MongoDBObject.newBuilder
        builder += "_id" -> ObjectId.get()
        builder += "user_id" -> (data \ "user" \ "user_id").asOpt[String].get
        builder += "company_id" -> (data \ "company" \ "company_id").asOpt[String].get

        builder.result
    }

    override def one2onesdr(obj: mongodb.casbah.Imports.DBObject): Map[String, JsValue] =
        Map(
            "condition" -> toJson(Map(
                "bind_id" -> toJson(obj.getAs[ObjectId]("_id").get.toString),
                "user_id" -> toJson(obj.getAs[String]("company_id").get),
                "company_id" -> toJson(obj.getAs[String]("company_id").get)
            ))
        )
}

object UserModule extends ModuleTrait {
    val ip = impl[user]
    val oo = impl[user2company]
    import ip._
    import oo._

    def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_pushUser(data) => pushMacro(d2m, ssr, data, names, name)
        case msg_popUser(data) => popMacro(qc, popr, data, names)
        case msg_queryUser(data) => queryMacro(qc, dr, MergeStepResult(data, pr), names, name)
        case msg_queryUserMulti(data) => queryMultiMacro(qcm, sr, MergeStepResult(data, pr), names, names)
        case msg_expendCompanyInfo(data) =>
            processor(value => returnValue(queryConnection(value)(pr)("user_company")))(MergeStepResult(data, pr))
        case msg_bindUserCompany(data) =>
            processor(value => returnValue(bindConnection(value)("user_company")))(MergeStepResult(data, pr))
        case msg_unbindUserCompany(data) =>
            processor(value => returnValue(unbindConnection(value)("user_company")))(MergeStepResult(data, pr))
        case _ => ???
    }
}
