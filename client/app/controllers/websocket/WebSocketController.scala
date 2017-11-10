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
import play.api.cache.Cache
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.json.Json._
import play.api.libs.streams.ActorFlow
import play.api.mvc._

import scala.concurrent.duration._
import scala.concurrent.Future
import scala.concurrent.stm.Ref
import scala.concurrent.stm.atomic

object SocketActorRef {
	val seq = Ref(Seq[(String, ActorRef)]())
}

class WebSocketController @Inject()
				(implicit system: ActorSystem, materializer: Materializer, dbt: dbInstanceManager, att: AuthTokenTrait)
				extends SameOriginCheck {
	
	def ws: WebSocket = WebSocket.accept[JsValue, JsValue] { request =>
		ActorFlow.actorRef ( out => WebSocketActor.props(out) )
	}
	
	def testBtn = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(MsgWebSocketTestBtn(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "acsystem" -> system ))))
	})
}

// TODO: 安全限制，以后测试与实现
trait SameOriginCheck {
	
	def sameOriginCheck(rh: RequestHeader): Boolean = {
		rh.headers.get("Origin") match {
			case Some(originValue) if originMatches(originValue) =>
				println(s"originCheck: originValue = $originValue")
				true
			
			case Some(badOrigin) =>
				println(s"originCheck: rejecting request because Origin header value ${badOrigin} is not in the same origin")
				false
			
			case None =>
				println("originCheck: rejecting request because no Origin header found")
				false
		}
	}
	
	def originMatches(origin: String): Boolean = {
		origin.contains("127.0.0.1:8000") || origin.contains("127.0.0.1:8001")
	}
}

object WebSocketActor {
	def props(out: ActorRef) = Props(new WebSocketActor(out))
}

class WebSocketActor(out: ActorRef) extends Actor {
	import SocketActorRef._
	
	def receive: Receive = {
		case msg: JsValue =>
			atomic { implicit thx =>
				seq() = seq.single.get :+ ("qp", out)
			}
			println("Fuck 02")
		case _ => throw new Exception("")
	}
	
	override def postStop(): Unit = {
		atomic { implicit thx =>
			seq() = seq.single.get.filterNot(x => x._2 == out)
		}
		println(seq.single.get)
		super.postStop()
	}
	
}