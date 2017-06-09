package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import com.pharbers.aqll.dbmodule.MongoDBModule
import play.api.mvc._
import controllers.common.requestArgsQuery
import com.pharbers.aqll.module.fopModule.{SliceUpload, fop}

class FopController@Inject()(as_inject : ActorSystem, mdb: MongoDBModule) extends Controller {
    implicit val db = mdb
    implicit val as = as_inject
    def uploadFile = Action { request =>
        requestArgsQuery().uploadRequestArgs(request)(SliceUpload.ManyFileSlice)
    }

    def downloadFile(name: String) = Action {
        Ok(fop.downloadFile(name)).as("excel/csv")
    }
}