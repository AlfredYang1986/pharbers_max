package module.business

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.pharbers.aqll.pattern.ModuleTrait
import com.pharbers.aqll.pattern.MessageDefines
import com.pharbers.aqll.pattern.CommonMessage

object ModelOperationModuleMessage {
	sealed class msg_mondelOperationBase extends CommonMessage
	case class msg_operation(data : JsValue) extends msg_mondelOperationBase
}

object ModelOperationModule extends ModuleTrait {
	import ModelOperationModuleMessage._
	import controllers.common.default_error_handler.f
	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]]) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_operation(data) => msg_operation_func(data)
		case _ => ???
	}
	
	def msg_operation_func(data : JsValue)(implicit error_handler : Int => JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		(Some(Map("operation" -> toJson("OK"))), None)
	}
}