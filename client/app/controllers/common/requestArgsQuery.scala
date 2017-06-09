package controllers.common

import javax.inject.Inject

import play.api.mvc._
import play.api.libs.json.JsValue
import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import akka.pattern.ask
import scala.concurrent.duration._
import scala.concurrent.Await
import com.pharbers.aqll.pattern.MessageRoutes
import com.pharbers.aqll.pattern.excute
import com.pharbers.aqll.pattern.RoutesActor
import play.api.libs.Files.TemporaryFile

object requestArgsQuery {
	def apply()(implicit akkasys : ActorSystem) = new requestArgsQuery()
}

class requestArgsQuery @Inject() (implicit akkasys : ActorSystem) extends Controller {
	implicit val t = Timeout(3 seconds)

	def requestArgs(request : Request[AnyContent])(func : JsValue => JsValue) : Result = {
		try {
			request.body.asJson.map { x =>
				Ok(func(x))
			}.getOrElse (BadRequest("Bad Request for input"))
		} catch {
			case _ : Exception => BadRequest("Bad Request for input")
		}
	}

	def requestArgsV2(request : Request[AnyContent])(func : JsValue => MessageRoutes) : Result = {
		try {
			request.body.asJson.map { x =>
				Ok(commonExcution(func(x)))
			}.getOrElse (BadRequest("Bad Request for input"))
		} catch {
			case _ : Exception => BadRequest("Bad Request for input")
		}
	}

	def commonExcution(msr : MessageRoutes) : JsValue = {
		val act = akkasys.actorOf(Props[RoutesActor])
		val r = act ? excute(msr)
		Await.result(r.mapTo[JsValue], t.duration)
	}

	def uploadRequestArgs(request : Request[AnyContent])(func : MultipartFormData[TemporaryFile] => JsValue) : Result = {
		try {
			request.body.asMultipartFormData.map { x =>
				Ok(func(x))
			}.getOrElse (BadRequest("Bad Request for input"))
		} catch {
			case _ : Exception => BadRequest("Bad Request for input")
		}
	}
}