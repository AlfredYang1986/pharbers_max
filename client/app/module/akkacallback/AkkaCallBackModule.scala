package module.akkacallback

import com.pharbers.ErrorCode
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait
import module.akkacallback.AkkaCallBackMessage.MsgAkkaCallBack
import play.api.mvc.WebSocket

object AkkaCallBackModule extends ModuleTrait {
	def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case MsgAkkaCallBack(data) => akkaCallBack(data)
		case _ => throw new Exception("function is not impl")
	}
	
	def akkaCallBack(data: JsValue)(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			WebSocket
			(Some(Map("" -> toJson(""))), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
}
