package module.wbesocket

import java.util.UUID

import akka.actor.{ActorRef, ActorSystem}
import com.pharbers.ErrorCode
import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.http.HTTP
import controllers.websocket.{SocketActorRef, WebSocketActor}
import module.wbesocket.WebSocketMessage.MsgWebSocketTestBtn
import play.api.cache.Cache
import play.api.Play.current
import play.api.libs.concurrent.InjectedActorSupport
import play.api.libs.json._
import play.api.libs.json.Json._

import scala.concurrent.stm.atomic

object WebSocketModule extends ModuleTrait with InjectedActorSupport{
	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case MsgWebSocketTestBtn(data) => testBtn(data)
		case _ => throw new Exception("function is not impl")
	}
	
	def testBtn(data: JsValue)(implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
//			val system = cm.modules.get("acsystem").asInstanceOf[ActorSystem]
			atomic { implicit thx =>
				SocketActorRef.seq.single.get.filterNot(x => x._1 != "qp").foreach { x =>
					x._2 ! toJson("Alex")
				}
			}
			(Some(Map("status" -> toJson("ok"))), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
}
