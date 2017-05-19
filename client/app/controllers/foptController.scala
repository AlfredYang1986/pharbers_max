package controllers

import play.api._
import play.api.mvc._
import common.requestArgsQuery.uploadRequestArgs
import common.default_error_handler.f
import com.pharbers.aqll.module.fopModule.{SliceUpload, fop}
import com.pharbers.aqll.pattern.CommonModule

class foptController extends Controller {
	implicit val cm = CommonModule(Some(Map("" -> None)))

	def uploadFile = Action { request =>
		uploadRequestArgs(request)(SliceUpload.ManyFileSlice)
	}

	def downloadFile(name : String) = Action{
		Ok(fop.downloadFile(name)).as("excel/xlsx")
	}

	def exportFile(name : String) = Action{
		Ok(fop.exportFile(name)).as("excel/csv")
	}

}