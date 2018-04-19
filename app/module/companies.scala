package module

import com.mongodb.casbah
import org.bson.types.ObjectId
import play.api.libs.json.JsValue
import com.mongodb.casbah.Imports
import com.mongodb.casbah.Imports._
import play.api.libs.json.Json.toJson
import module.CompanyMessage._
import module.common.processor._
import module.datamodel.basemodel
import com.pharbers.bmpattern.ModuleTrait
import module.stragety.{bind, impl, one2many}
import module.common.pharbersmacro.CURDMacro._
import module.common.{MergeStepResult, processor}
import com.pharbers.bmmessages.{CommonMessage, CommonModules, MessageDefines}
import module.users.user

abstract class msg_CompanyCommand extends CommonMessage("companies", CompanyModule)

object CompanyMessage {
    case class msg_pushCompany(data: JsValue) extends msg_CompanyCommand
    case class msg_popCompany(data : JsValue) extends msg_CompanyCommand
    case class msg_queryCompany(data : JsValue) extends msg_CompanyCommand
    case class msg_queryCompanyMulti(data : JsValue) extends msg_CompanyCommand

    case class msg_expendUsersInfo(data : JsValue) extends msg_CompanyCommand
}

class company extends basemodel {
    override val name: String = "company"
    override lazy val names: String = "companies"
    override def runtimeClass: Class[_] = classOf[company]

    val qc : JsValue => DBObject = { js =>
        val tmp = (js \ "condition" \ "company_id").asOpt[String].get
        DBObject("_id" -> new ObjectId(tmp))
    }

    val anqc: JsValue => _root_.com.mongodb.casbah.Imports.DBObject = { js =>
        val tmp = (js \ "condition" \ "company_id").asOpt[String].get
        DBObject("company_id" -> tmp)
    }

    val qcm : JsValue => DBObject = { js =>
        (js \ "condition" \ "companies").asOpt[List[String]].get match {
            case Nil => DBObject("query" -> "none")
            case ll : List[String] => $or(ll map (x => DBObject("_id" -> new ObjectId(x))))
        }
    }

    val ssr : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "company_id" -> toJson(obj.getAs[ObjectId]("_id").get.toString)
        )
    }

    val sr : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "company_id" -> toJson(obj.getAs[ObjectId]("_id").get.toString),
            "company_name" -> toJson(obj.getAs[String]("company_name").get),
            "company_des" -> toJson(obj.getAs[String]("company_des").get)
        )
    }

    val dr : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "company_id" -> toJson(obj.getAs[ObjectId]("_id").get.toString),
            "company_name" -> toJson(obj.getAs[String]("company_name").get),
            "company_des" -> toJson(obj.getAs[String]("company_des").get)
        )
    }

    val popr : DBObject => Map[String, JsValue] = { _ =>
        Map(
            "pop company" -> toJson("success")
        )
    }

    val d2m : JsValue => DBObject = { js =>
        val data = (js \ "company").asOpt[JsValue].map (x => x).getOrElse(toJson(""))

        val builder = MongoDBObject.newBuilder
        builder += "_id" -> ObjectId.get()      // user_id 唯一标示
        builder += "company_name" -> (data \ "company_name").asOpt[String].map (x => x).getOrElse("")
        builder += "company_des" -> (data \ "company_des").asOpt[String].map (x => x).getOrElse("")

        builder.result
    }

    val up2m : (DBObject, JsValue) => DBObject = { (obj, js) =>
        val data = (js \ "user").asOpt[JsValue].get

        (data \ "company_name").asOpt[String].map (x => obj += "company_name" -> x).getOrElse(Unit)
        (data \ "company_des").asOpt[String].map (x => obj += "company_des" -> x).getOrElse(Unit)

        obj
    }
}

class company2user extends one2many[company, user] with bind[company, user] {
    override def createThis: company = impl[company]
    override def createThat: user = impl[user]

    override def one2manyssr(obj: Imports.DBObject): Map[String, JsValue] =
        Map("_id" -> toJson(obj.getAs[String]("user_id").get))

    override def one2manyaggregate(lst: List[Map[String, JsValue]]): DBObject =
        $or(lst map (x => DBObject("_id" -> new ObjectId(x("_id").asOpt[String].get))))

    override def one2manysdr(obj: Imports.DBObject): Map[String, JsValue] =
        Map(
            "condition" -> toJson(Map(
                "bind_id" -> toJson(obj.getAs[ObjectId]("_id").get.toString),
                "user_id" -> toJson(obj.getAs[String]("company_id").get),
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
}

object CompanyModule extends ModuleTrait {
    val ip: company = impl[company]
    val oo: company2user = impl[company2user]
    import ip._
    import oo._

    def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_pushCompany(data) => pushMacro(d2m, ssr, data, names, name)
        case msg_popCompany(data) => popMacro(qc, popr, data, names)
        case msg_queryCompany(data) => queryMacro(qc, dr, MergeStepResult(data, pr), names, name)
        case msg_queryCompanyMulti(data) => queryMultiMacro(qcm, sr, MergeStepResult(data, pr), names, names)
        case msg_expendUsersInfo(data) =>
            processor(value => returnValue(queryConnection(value)(pr)("user_company")))(MergeStepResult(data, pr))
        case _ => ???
    }
}

