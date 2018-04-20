package module.users

import module.common.processor._
import module.common.stragety.impl
import module.users.UserMessage._
import play.api.libs.json.JsValue
import com.pharbers.bmpattern.ModuleTrait
import module.common.pharbersmacro.CURDMacro._
import module.common.{MergeStepResult, processor}
import com.pharbers.bmmessages.{CommonModules, MessageDefines}

object UserModule extends ModuleTrait {
    val ip: user = impl[user]
    val oo: user2company = impl[user2company]
    import ip._
    import oo.{checkExist => _, _}

    def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                   (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_verifyUserRegister(data) =>
            processor(value => returnValue(checkExist(value, pr, "user email has been use")(ckAttrExist, ssr, names)))(MergeStepResult(data, pr))
        case msg_pushUser(data) => pushMacro(d2m, ssr, data, names, name)
        case msg_popUser(data) => popMacro(qc, popr, data, names)
        case msg_queryUser(data) => queryMacro(qc, dr, MergeStepResult(data, pr), names, name)
        case msg_queryUserMulti(data) => queryMultiMacro(qcm, sr, MergeStepResult(data, pr), names, names)

        case msg_verifyCompanyBind(data) =>
            processor(value => returnValue(checkExist(value, pr, "user and company bind has been use")(checkBindExist, ssr, "user_company")))(MergeStepResult(data, pr))
        case msg_bindUserCompany(data) =>
            processor(value => returnValue(bindConnection(value)("user_company")))(MergeStepResult(data, pr))
        case msg_unbindUserCompany(data) =>
            processor(value => returnValue(unbindConnection(value)("user_company")))(MergeStepResult(data, pr))
        case msg_expendCompanyInfo(data) =>
            processor(value => returnValue(queryConnection(value)(pr)("user_company")))(MergeStepResult(data, pr))
        case _ => ???
    }
}
