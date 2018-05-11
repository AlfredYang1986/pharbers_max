package module.jobs

import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.pharbersmacro.CURDMacro._
import module.common.processor._
import module.common.stragety.impl
import module.common.{MergeStepResult, processor}
import module.jobs.JobMessage.{msg_bindJobUserPre, _}
import play.api.libs.json.{JsObject, JsValue}

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

        case msg_bindJobUserPre(data) =>
            processor(value => returnValue(oo.bindPre(value, pr, "job and user bind error")(oo.cbeIn, oo.cbeOut, "users")))(data)
        case msg_bindJobUser(data) =>
            processor(value => returnValue(bindConnection(value)("job_user")))(MergeStepResult(data, pr))
        case msg_unbindJobUser(data) =>
            processor(value => returnValue(unbindConnection(value)("job_user")))(MergeStepResult(data, pr))
        case msg_expendUserInfo(data) =>
            processor(value => returnValue(queryConnection(value)(pr)("job_user")))(MergeStepResult(data, pr))
        case msg_expendCompanyInfoByJob(data) => expendCompanyInfoByJob(MergeStepResult(data, pr))

        case msg_ymCalcJob(data) =>
            callJob((value, call) => callFunc(call, yf)(value))("ymCalc")(MergeStepResult(data, pr))
        case msg_panelJob(data) =>
            callJob((value, call) => callFunc(call, pf)(value))("panel")(MergeStepResult(data, pr))
        case msg_calcJob(data) =>
            callJob((value, call) => callFunc(call, nullFun)(value))("calc")(MergeStepResult(data, pr))
        case msg_killJob(data) =>
            callJob((value, call) => callFunc(call, nullFun)(value))("kill")(MergeStepResult(data, pr))

        case _ => ???
    }

    def expendCompanyInfoByJob(jv: JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val user = (jv \ "job" \ "user").as[JsObject]
        (Some(jv.as[JsObject].value.toMap ++ Map("user" -> user)), None)
    }

}
