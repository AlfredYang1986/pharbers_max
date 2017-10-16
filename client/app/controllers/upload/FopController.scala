package controllers.upload

import javax.inject.Inject

import akka.actor.ActorSystem
import com.pharbers.aqll.module.fopModule.{Upload, fop}
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.token.AuthTokenTrait
import controllers.common.requestArgsQuery
import play.api.mvc._

class FopController @Inject() (as_inject : ActorSystem, dbt : dbInstanceManager, att : AuthTokenTrait) extends Controller {
    
    implicit val as = as_inject
    def uploadFile = Action { request =>
        requestArgsQuery().uploadRequestArgs(request)(Upload.uploadFile)
    }
    
    def downloadFile(name: String) = Action {
        Ok(fop.downloadFile(name)).as("excel/csv")
    }
}