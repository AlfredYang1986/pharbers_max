package module.users

import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.common.MergeStepResult
import module.common.pharbersmacro.commonMacro._
import module.common.processor
import module.common.processor._
import module.users.UserData.{UserCondition, UserResult}
import module.users.UserMessage._
import play.api.libs.json.JsValue

object UserModule extends ModuleTrait {

    def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_pushUser(data) => pushMacro(inner_traits.d2m, inner_traits.ssr, data, "users", "user")
        case msg_popUser(data) => popMacro(inner_traits.qc, inner_traits.popr, data, "users")
        case msg_queryUser(data) => queryMacro(inner_traits.qc, inner_traits.dr, MergeStepResult(data, pr), "users", "user")
        case msg_queryUserMulti(data) => queryMultiMacro(inner_traits.qcm, inner_traits.sr, MergeStepResult(data, pr), "users", "users")
        case _ => ???
    }

    object inner_traits extends UserCreation with UserResult with UserCondition

//    def queryUserMulti(data : JsValue)
//                      (pr : Option[Map[String, JsValue]])
//                      (implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
//
//        import inner_traits.qcm
//        import inner_traits.sr
//        processor (value => returnValue(queryMulti(value)("users"), "users"))(MergeStepResult(data, pr))
//    }
}