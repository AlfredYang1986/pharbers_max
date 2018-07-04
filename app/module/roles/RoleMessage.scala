package module.roles

import play.api.libs.json.JsValue
import com.pharbers.bmmessages.CommonMessage

/**
  * Created by clock on 18-6-11.
  */
abstract class msg_RoleCommand extends CommonMessage("roles", RoleModule)

object RoleMessage {
    case class msg_verifyRoleRegister(data: JsValue) extends msg_RoleCommand
    case class msg_pushRole(data: JsValue) extends msg_RoleCommand
    case class msg_popRole(data : JsValue) extends msg_RoleCommand
    case class msg_queryRole(data : JsValue) extends msg_RoleCommand
    case class msg_queryRoleMulti(data : JsValue) extends msg_RoleCommand

    case class msg_bindUserRole(data: JsValue) extends msg_RoleCommand
    case class msg_unbindUserRole(data : JsValue) extends msg_RoleCommand
    case class msg_expendUsersInfo(data : JsValue) extends msg_RoleCommand
}