package module.callhttp

import com.pharbers.aqll.common.alCallHttp
import com.pharbers.aqll.common.alErrorCode.alErrorCode.{errorToJson, successToJson}
import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait
import module.callhttp.CallHttpMessage.MsgCallHttpServer
import play.api.libs.json.JsValue

import scala.collection.immutable.Map

object CallHttpModule extends ModuleTrait {
	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case MsgCallHttpServer(data: JsValue) => callHttpServer(data)
		case _ => throw new Exception("function is not impl")
	}
	
	def callHttpServer(data: JsValue)(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val businessType = (data \ "businessType").get.asOpt[String].getOrElse("error input")
			val result = alCallHttp(businessType, data).call
			val js_result = (result \ "result").get.asOpt[JsValue].get
			val js_status = (result \ "status").get.asOpt[String].get
			js_status match {
				case "success" => {
					val res_json = (js_result \ "result").get.asOpt[JsValue].get
					(successToJson(res_json), None)
				}
				case "error" => (None, Some(result))
			}
		} catch {
			case ex: Exception =>
				println(ex)
				(None, Some(errorToJson(ex.getMessage())))
		}
	}
}
