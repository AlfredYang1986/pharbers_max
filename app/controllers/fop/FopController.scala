package controllers.fop

import play.api.mvc._
import javax.inject.Inject

import akka.actor.ActorSystem
import com.pharbers.fopModule.{Download, Upload}
import controllers.common.requestArgsQuery

class FopController @Inject()(as_inject: ActorSystem) extends Controller {
    implicit val as = as_inject
    def uploadFile = Action { request =>
        requestArgsQuery().uploadRequestArgs(request)(Upload.uploadFile)
    }

    def downloadFile(name: String) = Action {
        Ok(Download.downloadFile(name)).as("excel/csv")
    }
}