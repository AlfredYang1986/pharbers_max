package controllers

import javax.inject.Inject

import com.pharbers.aqll.dbmodule.MongoDBModule
import play.api.mvc._
import common.requestArgsQuery.uploadRequestArgs
import common.default_error_handler.f
import com.pharbers.aqll.module.fopModule.{SliceUpload, fop}

class FopController@Inject()(mdb: MongoDBModule) extends Controller {
    implicit val db = mdb
    def uploadFile = Action { request =>
        uploadRequestArgs(request)(SliceUpload.ManyFileSlice)
    }

    def downloadFile(name: String) = Action {
        Ok(fop.downloadFile(name)).as("excel/csv")
    }
}