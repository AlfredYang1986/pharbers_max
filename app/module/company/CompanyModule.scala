package module.company

import module.common.stragety.impl
import module.common.processor._
import play.api.libs.json.JsValue
import module.company.CompanyMessage._
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.pharbersmacro.CURDMacro._
import module.common.{MergeStepResult, processor}
import com.pharbers.bmmessages.{CommonModules, MessageDefines}

object CompanyModule extends ModuleTrait {
    val ip: company = impl[company]
    val oo: company2user = impl[company2user]
    import ip._
    import oo._

    def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                   (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_verifyCompanyRegister(data) =>
            processor(value => returnValue(checkExist(value, pr, "company name has been use")(ckAttrExist, ssr, names)))(data)
        case msg_pushCompany(data) => pushMacro(d2m, ssr, data, names, name)
        case msg_popCompany(data) => popMacro(qc, popr, data, names)
        case msg_queryCompany(data) => queryMacro(qc, dr, MergeStepResult(data, pr), names, name)
        case msg_queryRegisterCompany(data) => queryMacro(qrc, dr, MergeStepResult(data, pr), names, name)
        case msg_queryCompanyMulti(data) => queryMultiMacro(qcm, sr, MergeStepResult(data, pr), names, names)
        case msg_expendUsersInfo(data) =>
            processor(value => returnValue(queryConnection(value)(pr)("user_company")))(MergeStepResult(data, pr))
        case _ => ???
    }
}

