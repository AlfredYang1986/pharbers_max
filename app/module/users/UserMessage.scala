package module.users

import play.api.libs.json.JsValue
import com.pharbers.bmmessages.CommonMessage

/**
  * Created by spark on 18-4-19.
  */
abstract class msg_UserCommand extends CommonMessage("users", UserModule)

object UserMessage {
    case class msg_verifyUserRegister(data: JsValue) extends msg_UserCommand
    case class msg_pushUser(data: JsValue) extends msg_UserCommand
    case class msg_popUser(data : JsValue) extends msg_UserCommand
    case class msg_queryUser(data : JsValue) extends msg_UserCommand
    case class msg_queryUserMulti(data : JsValue) extends msg_UserCommand

    case class msg_verifyCompanyBind(data: JsValue) extends msg_UserCommand
    case class msg_bindUserCompany(data : JsValue) extends msg_UserCommand
    case class msg_unbindUserCompany(data : JsValue) extends msg_UserCommand
    case class msg_expendCompanyInfo(data : JsValue) extends msg_UserCommand

    case class msg_expendJobsInfo(data : JsValue) extends msg_UserCommand

    case class msg_authWithPassword(data: JsValue) extends msg_UserCommand
    case class msg_authSetExpire(data: JsValue) extends msg_UserCommand
}