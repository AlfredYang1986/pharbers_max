package module.jobs.channel.callJobResponse

import module.jobs.job
import org.bson.types.ObjectId
import callJobResponseMessage._
import module.common.processor._
import com.mongodb.casbah.Imports
import play.api.libs.json.JsValue
import module.common.stragety.impl
import com.pharbers.bmpattern.ModuleTrait
import com.mongodb.casbah.Imports.DBObject
import com.pharbers.pharbersmacro.CURDMacro._
import module.common.{MergeStepResult, processor}
import com.pharbers.bmmessages.{CommonModules, MessageDefines}

object callJobResponseModule extends ModuleTrait {
    val ip: job = impl[job]
    import ip._

    val qrc : JsValue => DBObject = { js =>
        val tmp = (js \ "job_id").asOpt[String].get
        DBObject("_id" -> new ObjectId(tmp))
    }

    override def dispatchMsg(msg: MessageDefines)
                            (pr: Option[Map[String, JsValue]])
                            (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_queryJobResponse(data) =>
            queryMacro(qrc, dr, MergeStepResult(data, pr), names, name)
        case msg_changeJobStatus(data) =>
            updateMacro(qrc, choiceStatus(data), dr, data, names, name)

        case _ => ???
    }

    def choiceStatus(jv: JsValue): (Imports.DBObject, JsValue) => Imports.DBObject = {
        (jv \ "call").asOpt[String].get match {
            case "ymCalc" => up2ymed
            case "panel" => up2paneled
            case "calc" => up2calced
            case _ => ???
        }
    }
}
