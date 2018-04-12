package module.callhttp

import com.pharbers.bmmessages.CommonMessage
import play.api.libs.json.JsValue


abstract class MsgCallHttpCommand extends CommonMessage("callhttp", CallHttpModule)

object CallHttpMessage {
	case class MsgCallHttpServer(data: JsValue) extends MsgCallHttpCommand
}
