package controllers

import play.api._
import play.api.mvc._

import controllers.common.requestArgsQuery.uploadRequestArgs
import controllers.common.default_error_handler.f
import com.pharbers.aqll.module.fopModule.fop

object foptController extends Controller {
	def uploadFile = Action { request => 
		uploadRequestArgs(request)(fop.uploadFile) 
	}
	def downloadFile(name : String) = Action (Ok(fop.downloadFile(name)).as("image/png"))
}