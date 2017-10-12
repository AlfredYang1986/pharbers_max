package controllers

import javax.inject._

import akka.actor.ActorSystem
import com.pharbers.aqll.common._
import com.pharbers.cliTraits.DBTrait
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.mongodbConnect.connection_instance
import com.pharbers.token.AuthTokenTrait
import play.api.mvc._


// TODO 稍后进行封装
trait alValidationController { this: Controller =>
    def validation(parm: String)(implicit att: AuthTokenTrait): Result = {
        val token = att.decrypt2JsValue(parm)
        val temp = java.net.URLEncoder.encode(parm, "ISO-8859-1")
        
        val reVal = alValidationToken(parm)(att).validation
        reVal match {
            case TokenError() => Redirect("/error")
            case TokenFail() => Redirect("/token/fail")
            case TokenForgetPassword() => Redirect(s"/password/new/$temp/${(token \ "email").as[String]}")
            case TokenFirstLogin() => Redirect(s"/password/set/$temp/${(token \ "email").as[String]}")
        }
    }
    
    def validationPasswod(parm: String)(implicit att: AuthTokenTrait): Result = {
        val token = att.decrypt2JsValue(parm)
        val temp = java.net.URLEncoder.encode(parm, "ISO-8859-1")
        
        val reVal = alValidationToken(parm)(att).validation
        reVal match {
            case TokenError() => Redirect("/error")
            case TokenFail() => Redirect("/token/fail")
            case TokenForgetPassword() => Ok(views.html.authPages.newPassword((token \ "email").as[String]))
            case TokenFirstLogin() => Ok(views.html.authPages.setPassword((token \ "email").as[String]))
        }
    }
    
    def getUserTokenByCookies(request: Request[AnyContent]): String = {
        request.cookies.get("user_token").map(x => x.value).getOrElse("")
    }
    
    def loginForType(request: Request[AnyContent])(implicit att: AuthTokenTrait): Result = {
        val token = java.net.URLDecoder.decode(getUserTokenByCookies(request), "UTF-8")
        if ((att.decrypt2JsValue(token) \ "scope").asOpt[List[String]].getOrElse(Nil).contains("BD")) Redirect("/login/db")
        else Redirect("/index")
    }
}

class alMaxRouterController @Inject()(as_inject : ActorSystem, dbt : dbInstanceManager, att : AuthTokenTrait) extends Controller with alValidationController{
    implicit val as = as_inject
    implicit val db_cores : DBTrait = dbt.queryDBInstance("calc").get
    implicit val db_basic : DBTrait = dbt.queryDBInstance("cli").get

    implicit val db_cores_connection : connection_instance = dbt.queryDBConnection("calc").get
    implicit val db_basic_connection : connection_instance = dbt.queryDBConnection("cli").get
    implicit val attoken: AuthTokenTrait = att
    
    
    //从cookie中取出token验证用户角色
    def auth_user = Action { request => loginForType(request)}
    
    def validation_token(parm: String) = Action { request => validation(parm) }

    def login = Action { request =>
        Ok(views.html.authPages.login())
    }

    def infoRegistration = Action { request =>
        Ok(views.html.authPages.infoRegistration())
    }

    def dbLogin = Action { request =>
        Ok(views.html.authPages.bdSignSelect())
    }

    def userInfoConfirm = Action { request =>
        Ok(views.html.authPages.userInfoConfirm())
    }

    def verificationRegister = Action { request =>
        Ok(views.html.authPages.registerCodeValidation())
    }

    def tokenFail = Action { request =>
        Ok(views.html.authPages.activeAccountFailed())
    }

    def emailInvocation(name : String, email : String) = Action { request =>
        Ok(views.html.authPages.emailBeenSend(name, email))
    }

    def findpwd = Action{
        Ok(views.html.authPages.findPassword())
    }
    
    def findpwd_success = Action{
        Ok(views.html.authPages.findPasswordSuccess())
    }
    
    def new_pwd(token: String, email: String) = Action {
        validationPasswod(token)
    }
    
    def set_pwd(token: String, email: String) = Action {
        validationPasswod(token)
    }

    def index = Action { request =>
        Ok(views.html.calcPages.index())
    }
    
    def calcData = Action { request =>
        Ok(views.html.calcPages.newhome.calcData(""))
    }

    def historyData = Action { request =>
        Ok(views.html.calcPages.historyData())
    }
    
    def postSuccess = Action{
        Ok(views.html.authPages.successEmailPost())
    }

    def registerSuccess(name : String, email : String) = Action{
        Ok(views.html.authPages.successRegister(name, email))
    }
}