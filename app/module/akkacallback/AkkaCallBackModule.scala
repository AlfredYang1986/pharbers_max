package module.akkacallback

import com.pharbers.ErrorCode
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.message.send.SendMessageTrait
import com.pharbers.message.websocket.WebSocketOutActorRef
import module.akkacallback.AkkaCallBackMessage.MsgAkkaCallBack

object AkkaCallBackModule extends ModuleTrait {
	def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case MsgAkkaCallBack(data) => akkaCallBack(data)
		case _ => throw new Exception("function is not impl")
	}
	
	def akkaCallBack(data: JsValue)(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val msg = cm.modules.get("msg").asInstanceOf[SendMessageTrait]
			val uid = (data \ "condition" \ "uid").asOpt[String].getOrElse("")
			val json = (data \ "condition" \ "msg").get
			msg.wSocket.sendMsg(json, uid)
			(Some(Map("status" -> toJson("ok"))), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
}
