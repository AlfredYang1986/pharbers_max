package module

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

import com.pharbers.aqll.pattern.ModuleTrait
import com.pharbers.aqll.pattern.MessageDefines
import com.pharbers.aqll.pattern.CommonMessage

object ExampleModuleMessage {
	sealed class msg_exampleBase extends CommonMessage
	case class msg_example_1(data : JsValue) extends msg_exampleBase
	case class msg_example_2(data : JsValue) extends msg_exampleBase
	case class msg_error_emp(data : JsValue) extends msg_exampleBase
}

object ExampleModule extends ModuleTrait {
	import ExampleModuleMessage._
	import controllers.common.default_error_handler.f
	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]]) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_example_1(data) => msg_example_func(data)
		case msg_example_2(data) => msg_example_func_further_round(data)(pr)
		case msg_error_emp(data) => msg_error_emp(data)
		case _ => ???
	}
	
	def msg_example_func(data : JsValue)(implicit error_handler : Int => JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			(Some(Map("func1" -> toJson("ok"))), None)
			
		} catch {
			case ex : Exception => (None, Some(error_handler(ex.getMessage().toInt)))
		}
	}
	
	def msg_example_func_further_round(data : JsValue)(pr : Option[Map[String, JsValue]])(implicit error_handler : Int => JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val re = pr match {
				case Some(p) => p + ("func2" -> toJson("ok"))
				case None => Map("func2" -> toJson("ok"))
			}
			(Some(re), None)
			
		} catch {
			case ex : Exception => (None, Some(error_handler(ex.getMessage().toInt)))
		}	
	}
	
	def msg_error_emp(data : JsValue)(implicit error_handler : Int => JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			throw new Exception(-1.toString)
			(None, None)
			
		} catch {
			case ex : Exception => (None, Some(error_handler(ex.getMessage().toInt)))
		}		
	}

}