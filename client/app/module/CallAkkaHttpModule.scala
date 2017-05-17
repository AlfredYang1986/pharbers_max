package module

import com.pharbers.aqll.pattern.{CommonMessage, MessageDefines, ModuleTrait}
import play.api.libs.json.JsValue
import common.alCallHttp
/**
  * Created by qianpeng on 2017/2/13.
  */
object CallAkkaHttpModuleMessage {
	sealed class msg_CallHttp extends CommonMessage
	case class msg_callHttpServer(data: JsValue) extends msg_CallHttp
}

object CallAkkaHttpModule extends ModuleTrait {
	import CallAkkaHttpModuleMessage._
	import controllers.common.default_error_handler.f

	def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]]): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
			case msg_callHttpServer(data) => callHttpServer_func(data)
	}

	/**
		* @author liwei
		* @param data
		* @param error_handler
		* @return
		*/
	def callHttpServer_func(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val businessType = (data \ "businessType").get.asOpt[String].getOrElse("")
			(Some(Map("result" -> alCallHttp(businessType, data).call)), None)
		} catch {
			case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
		}
	}
}