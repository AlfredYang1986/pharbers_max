package module

import com.pharbers.aqll.pattern.{CommonMessage, CommonModule, MessageDefines, ModuleTrait}
import play.api.libs.json.JsValue
import common.alCallHttp
import com.pharbers.aqll.common.alErrorCode.alErrorCode._

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
	def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])(implicit cm : CommonModule): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
			case msg_callHttpServer(data) => callHttpServer_func(data)
			case _ => ???
	}

	/**
		* @author liwei
		* @param data
		* @return
		*/
	def callHttpServer_func(data: JsValue)(implicit error_handler: String => JsValue, cm: CommonModule): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val businessType = (data \ "businessType").get.asOpt[String].getOrElse("error input")
			(successToJson(alCallHttp(businessType, data).call), None)
		} catch {
			case ex: Exception => (None, Some(errorToJson(ex.getMessage())))
		}
	}
}