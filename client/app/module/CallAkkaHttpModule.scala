package module

import com.pharbers.aqll.pattern.{CommonMessage, MessageDefines, ModuleTrait}
import play.api.libs.json.JsValue
import common.alCallHttp
import com.pharbers.aqll.common.alErrorCode.alErrorCode._
import com.pharbers.aqll.dbmodule.MongoDBModule
/**
  * Created by qianpeng on 2017/2/13.
  */
object CallAkkaHttpModuleMessage {
	sealed class msg_CallHttp extends CommonMessage
	case class msg_callHttpServer(data: JsValue) extends msg_CallHttp
}

object CallAkkaHttpModule extends ModuleTrait {
	import CallAkkaHttpModuleMessage._
	def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])(implicit db: MongoDBModule): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_callHttpServer(data) => callHttpServer_func(data)
		case _ => ???
	}

	/**
		* @author liwei
		* @param data
		* @return
		*/
	def callHttpServer_func(data: JsValue)(implicit db: MongoDBModule): (Option[Map[String, JsValue]], Option[JsValue]) = {
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
			//@unit testing
			//(successToJson(toJson(Map("result" -> toJson(Map("status" -> toJson("success"),"message" -> toJson("201611#")))))), None)
		} catch {
			case ex: Exception => (None, Some(errorToJson(ex.getMessage())))
		}
	}
}
