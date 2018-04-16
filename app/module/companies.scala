package module

import com.mongodb.casbah.Imports._
import com.pharbers.bmmessages.{CommonMessage, CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.common.MergeStepResult
import module.CompanyMessage._
import module.common.pharbersmacro.CURDMacro._
import module.datamodel.basemodel
import org.bson.types.ObjectId
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import module.common.processor
import module.common.processor._
import module.stragety.impl

import scala.reflect.ClassTag

abstract class msg_CompanyCommand extends CommonMessage("companies", CompanyModule)

object CompanyMessage {
    case class msg_pushCompany(data: JsValue) extends msg_CompanyCommand
    case class msg_popCompany(data : JsValue) extends msg_CompanyCommand
    case class msg_queryCompany(data : JsValue) extends msg_CompanyCommand
    case class msg_queryCompanyMulti(data : JsValue) extends msg_CompanyCommand
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

object CompanyModule extends ModuleTrait {
    val ip = impl[company]
    import ip._

    def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_pushCompany(data) => pushMacro(d2m, ssr, data, names, name)
        case msg_popCompany(data) => popMacro(qc, popr, data, names)
        case msg_queryCompany(data) => queryMacro(qc, dr, MergeStepResult(data, pr), names, name)
        case msg_queryCompanyMulti(data) => queryMultiMacro(qcm, sr, MergeStepResult(data, pr), names, names)
        case _ => ???
    }
}

