package controllers.websocket

import javax.inject._

import akka.actor._
import akka.stream.Materializer
import com.pharbers.message.send.SendMessageTrait

import play.api.mvc._

class WebSocketController @Inject()(implicit system: ActorSystem, mat: Materializer, msg: SendMessageTrait) extends SameOriginCheck {
	
	def ws: WebSocket = msg.wSocket.connect
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