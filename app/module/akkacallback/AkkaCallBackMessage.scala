package module.akkacallback

import com.pharbers.bmmessages.CommonMessage
import play.api.libs.json.JsValue

abstract class MsgAkkaCallBackCommand extends CommonMessage("akkacallback", AkkaCallBackModule)

object AkkaCallBackMessage {
	case class MsgAkkaCallBack(data: JsValue) extends MsgAkkaCallBackCommand
}
