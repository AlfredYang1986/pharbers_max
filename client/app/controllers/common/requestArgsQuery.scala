package controllers.common

import play.api._
import play.api.mvc._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import akka.actor.Props
import akka.util.Timeout
import akka.pattern.ask
import scala.concurrent.duration._
import scala.concurrent.Await
import com.pharbers.aqll.pattern.MessageRoutes
import com.pharbers.aqll.pattern.excute
import com.pharbers.aqll.pattern.RoutesActor
import play.api.libs.Files.TemporaryFile
import play.api.libs.concurrent.Akka
import com.pharbers.aqll.common.alErrorCode.alErrorCode._

object requestArgsQuery extends Controller{
	implicit val t = Timeout(600 seconds)
  	def requestArgs(request : Request[AnyContent])(func : JsValue => MessageRoutes) : Result = {
  		try {
  			implicit val app = play.api.Play.current
  			request.body.asJson.map { x =>
  				Ok(commonExcution(func(x)))
  			}.getOrElse (BadRequest("Bad Request for input"))
  		} catch {
  			case _ : Exception => BadRequest("Bad Request for input")
  		}
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

  	def commonExcution(msr : MessageRoutes)(implicit app : Application) : JsValue = {
		val act = Akka.system(app).actorOf(Props[RoutesActor])
		val r = act ? excute(msr)
		Await.result(r.mapTo[JsValue], t.duration)
	}
}

object default_error_handler {
	implicit val f : String => JsValue = { name =>
		toJson(Map("status" -> toJson("error"), "result" -> toJson(Map("code" -> toJson(getErrorCodeByName(name)), "message" -> toJson(getErrorMessageByName(name))))))
	}
}