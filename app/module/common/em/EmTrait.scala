package module.common.em

import com.pharbers.common.xmpp.em.emDriver
import play.api.libs.json.{JsObject, JsValue}

trait EmTrait {
    val ed: emDriver = emDriver()

    // user manager
    def registerUser(jv: JsValue)(pr: JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val pwd = (jv \ "user" \ "password").asOpt[String].get
        val user_id = (pr \ "user" \ "user_id").asOpt[String].get
        ed.registerUser(user_id, pwd)
        (Some(pr.as[JsObject].value.toMap), None)
    }

    def deleteUser(jv: JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val user_id = (jv \ "condition" \ "user_id").asOpt[String].get
        ed.deleteUser(user_id)
        (Some(jv.as[JsObject].value.toMap), None)
    }

    def disconnectUser(jv: JsValue) = ???
    def modifyPossword(jv: JsValue) = ???

    // chatgroup manager
    def queryAllChatgroup(jv: JsValue) = ???
    def queryGroupIdByName(jv: JsValue) = ???

    def createChatgroup(jv: JsValue)(pr: JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val company_id = (pr \ "company" \ "company_id").asOpt[String].get
        val company_des = (jv \ "company" \ "company_des").asOpt[String].get
        ed.createChatgroup(company_id, company_des)
        (Some(pr.as[JsObject].value.toMap), None)
    }

    def deleteChatgroup(jv: JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val company_id = (jv \ "condition" \ "company_id").asOpt[String].get
        ed.deleteChatgroup(company_id)
        (Some(jv.as[JsObject].value.toMap), None)
    }

    // user and chatgroup relation
    def userJoinChatgroup(jv: JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val user_id = (jv \ "user" \ "user_id").asOpt[String].get
        val company_id = (jv \ "company" \ "company_id").asOpt[String].get
        ed.userJoinChatgroup(user_id, company_id)
        (Some(jv.as[JsObject].value.toMap), None)
    }

    def userQuitChatgroup(jv: JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = ???

}
