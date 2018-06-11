package module.users

import module.common.processor._
import module.users.UserMessage._
import play.api.libs.json.JsValue
import module.common.stragety.impl
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.pharbersmacro.CURDMacro._
import module.common.{MergeStepResult, processor}
import com.pharbers.bmmessages.{CommonModules, MessageDefines}

object UserModule extends ModuleTrait {
    val ip: user = impl[user]
    val uc: user2company = impl[user2company]
    val uj: user2job = impl[user2job]
    val ur: user2role = impl[user2role]

    import ip._

    def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                   (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_verifyUserRegister(data) =>
            processor(value => returnValue(checkExist(value, pr, "user email has been use")(ckAttrExist, ssr, names)))(data)
        case msg_pushUser(data) => pushMacro(d2m, ssr, MergeStepResult(data, pr), names, name)
        case msg_popUser(data) => popMacro(qc, popr, data, names)
        case msg_queryUser(data) => queryMacro(qc, dr, MergeStepResult(data, pr), names, name)
        case msg_queryUserMulti(data) => queryMultiMacro(qcm, sr, MergeStepResult(data, pr), names, names)

        case msg_bindUserCompanyPre(data) =>
            processor(value => returnValue(uc.bindPre(value, pr, "user and company bind error")(uc.cbeIn, uc.cbeOut, "companies")))(data)
        case msg_bindUserCompany(data) =>
            processor(value => returnValue(uc.bindConnection(value)("user_company")))(MergeStepResult(data, pr))
        case msg_unbindUserCompany(data) =>
            processor(value => returnValue(uc.unbindConnection(value)("user_company")))(MergeStepResult(data, pr))
        case msg_expendCompanyInfo(data) =>
            processor(value => returnValue(uc.queryConnection(value)(pr)("user_company")))(MergeStepResult(data, pr))
        case msg_expendJobsInfo(data) =>
            processor(value => returnValue(uj.queryConnection(value)(pr)("job_user")))(MergeStepResult(data, pr))
        case msg_expendRolesInfo(data) =>
            processor(value => returnValue(ur.queryConnection(value)(pr)("user_role")))(MergeStepResult(data, pr))

        case msg_authWithPassword(data) =>
            processor(value => returnValue(authWithPassword(authPwd, dr)(value)(names)))(MergeStepResult(data, pr))
        case msg_authSetExpire(data) =>
            processor(value => returnValue(authSetExpire(value)))(MergeStepResult(data, pr))

        case _ => ???
    }
}
