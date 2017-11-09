package controllers.websocket

import javax.inject._

import akka.actor._
import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.token.AuthTokenTrait
import controllers.common.requestArgsQuery
import module.wbesocket.WebSocketMessage.MsgWebSocketTestBtn
import play.api.Logger
import play.api.libs.json._
import play.api.libs.json.Json._
import play.api.libs.streams.ActorFlow
import play.api.mvc._

import scala.concurrent.duration._

import scala.concurrent.Future

class WebSocketController @Inject()(implicit system: ActorSystem, materializer: Materializer, dbt : dbInstanceManager, att : AuthTokenTrait) extends SameOriginCheck {
	val logger = play.api.Logger(getClass)
	
	
	def ws: WebSocket = WebSocket.accept[JsValue, JsValue] { request =>
		ActorFlow.actorRef{ out =>
			println(out)
			MyWebSocketActor.props(out)
		}
	}
	
	def testBtn = Action(request => requestArgsQuery().requestArgsV2(request) {jv =>
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(MsgWebSocketTestBtn(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "acsystem" -> system ))))/*"out" -> sact*/
	})
}
// TODO: 安全限制，以后测试与实现
trait SameOriginCheck {
	
	def logger: Logger
	
	def sameOriginCheck(rh: RequestHeader): Boolean = {
		rh.headers.get("Origin") match {
			case Some(originValue) if originMatches(originValue) =>
				logger.debug(s"originCheck: originValue = $originValue")
				true
			
			case Some(badOrigin) =>
				logger.error(s"originCheck: rejecting request because Origin header value ${badOrigin} is not in the same origin")
				false
			
			case None =>
				logger.error("originCheck: rejecting request because no Origin header found")
				false
		}
	}
	
	def originMatches(origin: String): Boolean = {
		origin.contains("127.0.0.1:8000") || origin.contains("127.0.0.1:8001")
	}
}

object MyWebSocketActor {
	def props(out: ActorRef) = Props(new MyWebSocketActor(out))
}

class MyWebSocketActor(out: ActorRef) extends Actor {
//	import scala.concurrent.ExecutionContext.Implicits.global
//	context.system.scheduler.schedule(2 seconds, 1 seconds, self, toJson("Alex"))
	
	def receive: Receive = {
		case msg: String => out ! msg
		case msg: JsValue => out ! msg
	}
}