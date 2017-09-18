package bmpattern


import play.api.libs.json.JsValue
import bmmessages.{CommonMessage, CommonModules, MessageDefines, MessageRoutes}

object ParallelMessage {
	implicit val f : List[Map[String, JsValue]] => Map[String, JsValue] = lst => {
		var re : Map[String, JsValue] = Map.empty
		lst foreach (m => m.foreach(kvs => re += kvs._1 -> kvs._2))
		re
	}
}

case class ParallelMessage(msgs : List[MessageRoutes],
						   merge : List[Map[String, JsValue]] =>
										Option[Map[String, JsValue]] =>
										Map[String, JsValue]) extends MessageDefines
case class ParalleMessageSuccess(r : Map[String, JsValue]) extends CommonMessage
case class ParalleMessageFailed(e : JsValue) extends CommonMessage