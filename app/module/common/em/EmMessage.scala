package module.common.em

import play.api.libs.json.JsValue
import com.pharbers.bmmessages.CommonMessage

/**
  * Created by clock on 18-5-15.
  */
abstract class msg_EmCommand extends CommonMessage("em", EmModule)

object EmMessage {

    // user manager
    case class msg_registerUserForEm(data: JsValue) extends msg_EmCommand
    case class msg_deleteUserForEm(data: JsValue) extends msg_EmCommand
    case class msg_disconnectUserForEm(data: JsValue) extends msg_EmCommand
    case class msg_modifyPosswordForEm(data: JsValue) extends msg_EmCommand

    // chatgroup manager
    case class msg_queryAllChatgroupForEm(data: JsValue) extends msg_EmCommand
    case class msg_queryGroupIdByNameForEm(data: JsValue) extends msg_EmCommand
    case class msg_createChatgroupForEm(data: JsValue) extends msg_EmCommand
    case class msg_deleteChatgroupForEm(data: JsValue) extends msg_EmCommand

    // user and chatgroup relation
    case class msg_userJoinChatgroupForEm(data: JsValue) extends msg_EmCommand
    case class msg_userQuitChatgroupForEm(data: JsValue) extends msg_EmCommand
}