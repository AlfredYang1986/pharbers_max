package com.pharbers.aqll.pattern

import play.api.libs.json.JsValue

object ParallelMessage {
	implicit val f : List[Map[String, JsValue]] => Map[String, JsValue] = lst => {
		var re : Map[String, JsValue] = Map.empty
		lst foreach (m => m.foreach(kvs => re += kvs._1 -> kvs._2))
		re
	}
}

case class ParallelMessage(msgs : List[MessageRoutes]) extends MessageDefines
case class ParalleMessageSuccess(r : Map[String, JsValue]) extends CommonMessage
case class ParalleMessageFailed(e : JsValue) extends CommonMessage