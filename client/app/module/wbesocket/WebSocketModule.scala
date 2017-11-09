package module.wbesocket

import java.util.UUID

import akka.actor.{ActorRef, ActorSystem}
import com.pharbers.ErrorCode
import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait
import controllers.websocket.MyWebSocketActor
import module.wbesocket.WebSocketMessage.MsgWebSocketTestBtn
import play.api.libs.concurrent.InjectedActorSupport
import play.api.libs.json._
import play.api.libs.json.Json._

import scala.collection.immutable.Map

object WebSocketModule extends ModuleTrait with InjectedActorSupport{
	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case MsgWebSocketTestBtn(data) => testBtn(data)
		case _ => throw new Exception("function is not impl")
	}
	
	def testBtn(data: JsValue)(implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val system = cm.modules.get("acsystem").asInstanceOf[ActorSystem]
			val sact = cm.modules.get("out").asInstanceOf[ActorRef]
			val act = system.actorOf(MyWebSocketActor.props(sact))
			act ! toJson("Alex")
			(Some(Map("status" -> toJson("ok"))), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
}
