package module.immessage

import com.pharbers.bmmessages.CommonMessage
import play.api.libs.json.JsValue


abstract class MsgImRoomCommand extends CommonMessage("imroom", ImRoomModule)

object ImRoomMessage {
	case class MsgCreateImRooms(data: JsValue) extends MsgImRoomCommand
}