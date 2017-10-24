package module.history

import com.pharbers.bmmessages.CommonMessage
import play.api.libs.json.JsValue


abstract class MsgHistoryCommand extends CommonMessage("history", HistoryModule)
object HistoryMessage {
	case class MsgQueryHistoryByPage(data: JsValue) extends MsgHistoryCommand
}
