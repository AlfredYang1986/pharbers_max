package module.jobs

import module.jobs.jobStatus._
import module.jobs.JobMessage._
import module.common.processor._
import play.api.libs.json.JsValue
import module.common.stragety.impl
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.pharbersmacro.CURDMacro._
import module.common.{MergeStepResult, processor}
import com.pharbers.bmmessages.{CommonModules, MessageDefines}

object JobModule extends ModuleTrait {
    val ip: job = impl[job]
    val oo: job2user = impl[job2user]
    import ip._
    import oo._

    def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                   (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_pushJob(data) => pushMacro(d2m, ssr, data, names, name)
        case msg_popJob(data) => popMacro(qc, popr, data, names)
        case msg_queryJob(data) => queryMacro(qc, dr, MergeStepResult(data, pr), names, name)
        case msg_queryJobMulti(data) => queryMultiMacro(qcm, sr, MergeStepResult(data, pr), names, names)

        case msg_verifyUserBind(data) =>
            processor(value => returnValue(checkExist(value, pr, "job and user bind has been use")(checkBindExist, ssr, "job_user")))(MergeStepResult(data, pr))
        case msg_bindJobUser(data) =>
            processor(value => returnValue(bindConnection(value)("job_user")))(MergeStepResult(data, pr))
        case msg_unbindJobUser(data) =>
            processor(value => returnValue(unbindConnection(value)("job_user")))(MergeStepResult(data, pr))
        case msg_expendUserInfo(data) =>
            processor(value => returnValue(queryConnection(value)(pr)("job_user")))(MergeStepResult(data, pr))

        case msg_panelJob(data) => updateMacro(qc, up2panel, dr, data, names, name)
        case msg_calcJob(data) => updateMacro(qc, up2calc, dr, data, names, name)
        case msg_killJob(data) => updateMacro(qc, up2kill, dr, data, names, name)

        case _ => ???
    }
}
