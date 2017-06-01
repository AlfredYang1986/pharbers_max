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

	def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModule): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
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
			val result = alCallHttp(businessType, data).call
			val res_json = (result \ "result").get.asOpt[JsValue].get
			val res_stat = (res_json \ "status").get.asOpt[String].get
			println(s"res_json=$res_json")
			res_stat match {
				case "success" => {
					val res_valu = (((res_json \ "result").get.asOpt[JsValue].get) \ "result").get.asOpt[JsValue].get
					(successToJson(res_valu), None)
				}
				case "error" => (None, Some(res_json))
			}
			//@unit testing
			//(successToJson(toJson(Map("result" -> toJson(Map("status" -> toJson("success"),"message" -> toJson("201611#")))))), None)
		} catch {
			case ex: Exception => (None, Some(errorToJson(ex.getMessage())))
		}
	}
}
