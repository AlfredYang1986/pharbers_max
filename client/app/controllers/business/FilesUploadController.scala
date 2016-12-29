package controllers.business

import play.api._
import play.api.mvc._

import controllers.common.requestArgsQuery.uploadRequestArgs
import controllers.common.default_error_handler.f
import com.pharbers.aqll.module.fopModule.fop

object FilesUploadController extends Controller {
	def uploadFile = Action { request => 
		uploadRequestArgs(request)(fop.uploadFile) 
	}
	def downloadFile(name : String) = Action (Ok(fop.downloadFile(name)).as("excel/xlsx"))
}