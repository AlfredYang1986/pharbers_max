package controllers.websocket

import javax.inject._

import akka.actor._
import akka.stream.Materializer
import com.pharbers.message.send.SendMessageTrait

import play.api.mvc._

class WebSocketController @Inject()(implicit system: ActorSystem, mat: Materializer, msg: SendMessageTrait) {
	def ws: WebSocket = msg.wSocket.connect
	
}