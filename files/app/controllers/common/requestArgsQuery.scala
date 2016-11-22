package controllers.common

import play.api._
import play.api.mvc._

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import play.api.libs.Files.TemporaryFile

import com.pharbers.aqll.util.errorcode.ErrorCode.errorMessageByCode

object requestArgsQuery extends Controller {
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

object default_error_handler {
	implicit val f : Int => JsValue = { code => 
		val (c, m) = errorMessageByCode(code)
		toJson(Map("status" -> toJson("error"), "error" -> toJson(Map("code" -> toJson(c), "message" -> toJson(m)))))
	}
}