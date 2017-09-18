package bmpattern

import play.api.libs.json.JsValue
import bmmessages.MessageDefines
import bmmessages.CommonModules

trait ModuleTrait {
	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue])
}