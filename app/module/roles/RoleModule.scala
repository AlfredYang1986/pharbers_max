package module.roles

import module.common.processor._
import module.roles.RoleMessage._
import module.common.stragety.impl
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.pharbersmacro.CURDMacro._
import play.api.libs.json.{JsObject, JsValue}
import module.common.{MergeStepResult, processor}
import com.pharbers.bmmessages.{CommonModules, MessageDefines}

object RoleModule extends ModuleTrait {
    val ip: role = impl[role]
    val oo: role2user = impl[role2user]
    import ip._
    import oo._

    def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                   (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_verifyRoleRegister(data) =>
            processor(value => returnValue(checkExist(value, pr, "role name has been use")(ckAttrExist, ssr, names)))(data)
        case msg_pushRole(data) => pushMacro(d2m, ssr, data, names, name)
        case msg_popRole(data) => popMacro(qc, popr, data, names)
        case msg_queryRole(data) => queryMacro(qc, dr, MergeStepResult(data, pr), names, name)
        case msg_queryRoleMulti(data) => queryMultiMacro(qcm, sr, MergeStepResult(data, pr), names, names)

        case msg_bindUserRole(data) =>
            processor(value => returnValue(bindConnection(value)("user_role")))(MergeStepResult(data, pr))
        case msg_unbindUserRole(data) =>
            processor(value => returnValue(unbindConnection(value)("user_role")))(MergeStepResult(data, pr))
        case msg_expendUsersInfo(data) =>
            processor(value => returnValue(queryConnection(value)(pr)("user_role")))(MergeStepResult(data, pr))

        case _ => ???
    }

}
