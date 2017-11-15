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
    def validation(parm: String)(implicit att: AuthTokenTrait, db: DBTrait): Result = {
        alParsingTokenUser(parm).parse match {
            case TokenFail() => Redirect("/token/fail")
            case User(name, email, phone, scope) =>
                val encode = java.net.URLEncoder.encode(parm, "ISO-8859-1")
                alValidationToken(parm)(att).validation match {
                    case TokenError() => Redirect("/error")
                    case TokenFail() => Redirect("/token/fail")
                    case TokenForgetPassword() => Redirect(s"/password/new/$encode/$email")
                    case TokenFirstLogin() => Redirect(s"/password/set/$encode/$email")
                }
        }
    }
    
    def validationPasswrod(parm: String)(implicit att: AuthTokenTrait, db: DBTrait): Result = {
        alParsingTokenUser(parm).parse match {
            case TokenFail() => Redirect("/token/fail")
            case User(name, email, phone, scope) =>
                alValidationToken(parm)(att).validation match {
                    case TokenError() => Redirect("/error")
                    case TokenFail() => Redirect("/token/fail")
                    case TokenForgetPassword() => Ok(views.html.authPages.newPassword(email))
                    case TokenFirstLogin() => Ok(views.html.authPages.setPassword(email))
                }
        }
    }
    
    def getUserTokenByCookies(request: Request[AnyContent]): String = request.cookies.get("user_token").map(x => x.value).getOrElse("")
    
    def loginForType(request: Request[AnyContent])(implicit att: AuthTokenTrait, db: DBTrait): Result = {
        if(showUser(request).scope.contains("BD")) Redirect("/login/db")
//        else Redirect("/index")
        else Redirect("/calcul/home")
    }
    
    def showUser(request: Request[AnyContent])(implicit att: AuthTokenTrait, db: DBTrait): User = {
        val token = java.net.URLDecoder.decode(getUserTokenByCookies(request), "UTF-8")
        alParsingTokenUser(token).parse match {
            case User(name, email, phone, scope) => User(name, email, phone, scope)
            case _ => ???
        }
    }
}

class alMaxRouterController @Inject()(as_inject : ActorSystem, dbt : dbInstanceManager, att : AuthTokenTrait) extends Controller with alValidationController{
    implicit val as = as_inject
    implicit val db_basic : DBTrait = dbt.queryDBInstance("cli").get
    implicit val attoken: AuthTokenTrait = att
    
    def test =Action {
        Ok(views.html.test())
    }
    
    //---------------------------------calcul--------------------------
    def cHome = Action{request =>
        Ok(views.html.calculPages.cHome(showUser(request).name))
    }
    def calculStep = Action{
        Ok(views.html.calculPages.calculStep.firstStep())
    }
    //---------------------------------bd----------------------------
    def bdUser = Action{
        Ok(views.html.bdPages.bdUser())
    }
    
    def addMember = Action{
        Ok(views.html.bdPages.addMember())
    }
    def addMember_succ(name : String, email : String) = Action{
        Ok(views.html.bdPages.addMember_succ(name, email))
    }
    def setInfo = Action{
        Ok(views.html.bdPages.userInfo())
    }
    def setbdPassword = Action{
        Ok(views.html.bdPages.setPassword())
    }

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
        Ok(views.html.authPages.bdSignSelect(showUser(request).phone))
    }

    def userInfoConfirm = Action { request =>
        Ok(views.html.authPages.userInfoConfirm())
    }

    def verificationRegister = Action { request =>
        Ok(views.html.authPages.registerCodeValidation())
    }

    def tokenFail = Action { request  =>
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
        validationPasswrod(token)
    }
    
    def set_pwd(token: String, email: String) = Action {
        validationPasswrod(token)
    }

    def index = Action { request =>
        
        Ok(views.html.calcPages.index(showUser(request).name))
    }
    
    def calcData = Action { request =>
        Ok(views.html.calcPages.newhome.calcData("", showUser(request).name))
    }

    def historyData = Action { request =>
        Ok(views.html.calcPages.hsitory.historyData(showUser(request).name))
    }
    
    def postSuccess = Action{
        Ok(views.html.authPages.successEmailPost())
    }

    def registerSuccess(name : String, email : String) = Action{
        Ok(views.html.authPages.successRegister(name, email))
    }
}