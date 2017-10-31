package controllers.upload

import javax.inject.Inject

import akka.actor.ActorSystem
import com.pharbers.aqll.module.fopModule.{Upload, fop}
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.token.AuthTokenTrait
import controllers.common.requestArgsQuery
import module.auth.AuthMessage.{msg_auth_token_expire, msg_auth_token_parser}
import module.upload.UploadMessage._
import module.users.UserMessage.msg_user_token_op
import play.api.libs.json.Json.toJson
import play.api.mvc._

class FopController @Inject() (as_inject : ActorSystem, dbt : dbInstanceManager, att : AuthTokenTrait) extends Controller {
    
    implicit val as = as_inject
    def uploadFile = Action { request =>
        requestArgsQuery().uploadRequestArgs(request)(Upload.uploadFile)
    }
    
    def queryUserCompnay = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
        import com.pharbers.bmpattern.LogMessage.common_log
        import com.pharbers.bmpattern.ResultMessage.common_result
    
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("upload"))), jv)
            :: msg_auth_token_parser(jv)
            :: msg_auth_token_expire(jv)
            :: msg_user_token_op(jv)
            :: msgQueryWithUserCompanyForUpload(jv)
            :: msg_CommonResultMessage()
            :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    
    })
    
    def downloadFile(name: String) = Action {
        Ok(fop.downloadFile(name)).as("excel/csv")
    }
}