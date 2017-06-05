package com.pharbers.aqll.pattern

import play.api.libs.json.JsValue
import com.pharbers.aqll.dbmodule.MongoDBModule

trait ModuleTrait {

	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit db: MongoDBModule) : (Option[Map[String, JsValue]], Option[JsValue])
}