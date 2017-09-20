package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import com.pharbers.aqll.dbmodule.db.DBTrait
import com.pharbers.token.AuthTokenTrait
import play.api.mvc._
import controllers.common.requestArgsQuery
import com.pharbers.aqll.module.fopModule.{SliceUpload, fop}

class FopController@Inject()(as_inject : ActorSystem, dbt : DBTrait, att : AuthTokenTrait) extends Controller {
    implicit val as = as_inject
    def uploadFile = Action { request =>
        requestArgsQuery().uploadRequestArgs(request)(SliceUpload.ManyFileSlice)
    }
    
    def upload = Action(parse.multipartFormData) { request =>
        request.body.file("file").map { file =>
            import java.io.File
            val filename = file.filename
            val contentType = file.contentType
            file.ref.moveTo(new File(s"/Users/qianpeng/FileBase/fea9f203d4f593a96f0d6faa91ba24ba/Client/CPA/$filename"))
            Ok("File uploaded")
        }.getOrElse {
            Ok("File uploaded")
        }
    }
    
    def downloadFile(name: String) = Action {
        Ok(fop.downloadFile(name)).as("excel/csv")
    }
}