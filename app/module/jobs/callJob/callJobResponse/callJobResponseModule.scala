package module.jobs.callJob.callJobResponse

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

object callJobResponseModule extends ModuleTrait with getJV2Map{
    val ip: job = impl[job]
    import ip._

    val qrc : JsValue => DBObject = { js =>
        val tmp = getArgs2Map(js)
        DBObject("_id" -> new ObjectId(tmp("job_id")))
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
        ((jv \ "call").as[String], (jv \ "stage").as[String]) match {
            case ("ymCalc", "start") => up2ym
            case ("ymCalc", "done") => up2ymed
            case ("panel", "start") => up2panel
            case ("panel", "done") => up2paneled
            case ("calc", "start") => up2calc
            case ("calc", "done") => up2calced
            case _ => ???
        }
    }
}
