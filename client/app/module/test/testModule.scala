package module.test

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.pharbers.aqll.pattern.MessageDefines
import com.pharbers.aqll.pattern.ModuleTrait

import testMessages._

object testModule extends ModuleTrait {
		
	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]]) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_Test_1(data) => test1(data)(pr)
		case msg_Test_2(data) => test2(data)(pr)
		case msg_Test_3(data) => test3(data)(pr)
		case _ => ???
	}
	
	def test1(data : JsValue)(pr : Option[Map[String, JsValue]]) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		(Some(Map("test1" -> toJson("test1"))), None)
	}

	def test2(data : JsValue)(pr : Option[Map[String, JsValue]]) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		(Some(Map("test2" -> toJson("test2"))), None)
	}

	def test3(data : JsValue)(pr : Option[Map[String, JsValue]]) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		(Some(pr.get), None)
	}
}