package module.wbesocket

import com.pharbers.bmmessages.CommonMessage
import play.api.libs.json.JsValue

abstract class MsgWebSocketCommand extends CommonMessage("websocket", WebSocketModule)

object WebSocketMessage {
	case class MsgWebSocketTestBtn(data: JsValue) extends MsgWebSocketCommand
}
