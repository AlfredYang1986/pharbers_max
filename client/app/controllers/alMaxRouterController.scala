package controllers

import java.util.Date
import javax.inject._

import akka.actor.ActorSystem
import com.pharbers.aqll.common.{alAdminEnum, alModularEnum}
import com.pharbers.cliTraits.DBTrait
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.mongodbConnect.connection_instance
import com.pharbers.token.AuthTokenTrait
import module.common.alPageDefaultData._
import play.api.mvc._

class alMaxRouterController @Inject()(as_inject : ActorSystem, dbt : dbInstanceManager, att : AuthTokenTrait) extends Controller {
    implicit val as = as_inject
    implicit val db_cores : DBTrait = dbt.queryDBInstance("calc").get
    implicit val db_basic : DBTrait = dbt.queryDBInstance("cli").get

    implicit val db_cores_connection : connection_instance = dbt.queryDBConnection("calc").get
    implicit val db_basic_connection : connection_instance = dbt.queryDBConnection("cli").get
    
    //从cookie中取出token验证用户角色
    def auth_user = Action { request =>
        val token = java.net.URLDecoder.decode(getUserTokenByCookies(request), "UTF-8")
        if ((att.decrypt2JsValue(token) \ "scope").asOpt[List[String]].getOrElse(Nil).contains("BD")) Redirect("/login/db")
        else Redirect("/index")
    }
    
    //验证GET URL中的功能对应跳转页面
    def validation_token(parm: String) = Action { request =>
        val token = att.decrypt2JsValue(parm)
        val temp = java.net.URLEncoder.encode(parm, "ISO-8859-1")
        val expire_in = (token \ "expire_in").asOpt[Long].map (x => x).getOrElse(throw new Exception("token parse error"))
        if (new Date().getTime > expire_in) Redirect("/token/fail")
        else
        (token \ "action").asOpt[String].getOrElse(None) match {
            case None => Redirect("/error")
            case "forget_password" => Redirect(s"/password/new/$temp")
            case "first_login" => Redirect(s"/password/set/$temp/${(token \ "email").as[String]}")
        }
    }

    //登录
    def login = Action { request =>
        Ok(views.html.login())
    }

    def infoRegistration = Action { request =>
        Ok(views.html.infoRegistration())
    }

    def dbLogin = Action { request =>
        Ok(views.html.bdSignSelect())
    }

    def userInfoConfirm = Action { request =>
        Ok(views.html.userInfoConfirm())
    }
    //验证授权码
    def verificationRegister = Action { request =>
        Ok(views.html.registerCodeValidation())
    }
    //过期
    def tokenFail = Action { request =>
        Ok(views.html.fail_token())
    }
    //邮箱激活
    def emailInvocation(name : String, email : String) = Action { request =>
        Ok(views.html.emailBeenSend(name, email))
    }

    //密码
    def findpwd = Action{
        Ok(views.html.findPassword())
    }
    def findpwd_success = Action{
        Ok(views.html.findPasswordSuccess())
    }
    def new_pwd(token: String) = Action{
        Ok(views.html.newPassword())
    }
    def set_pwd(token: String, email: String) = Action{
        Ok(views.html.setPassword(email))
    }

    //首页
    def index = Action { request =>
//        if (getUserTokenByCookies(request).equals("")) {
//            Ok(views.html.login())
//        } else {
//            Ok(views.html.index(getAdminByCookies(request)))
            Ok(views.html.index())
//        }
    }
    
    
    //计算2
    def calcData = Action { request =>
//        Ok(views.html.newhome.calcData(getAdminByCookies(request)))
        Ok(views.html.newhome.calcData(""))
    }

    //历史数据
    def historyData = Action { request =>
        Ok(views.html.HistoryData())
//            getAdminByCookies(request),
//            PageDefaultData(alModularEnum.RQ, db_basic_connection, db_basic_connection)._1))
    }
    

    def getUserTokenByCookies(request: Request[AnyContent]): String = {
        request.cookies.get("user_token").map(x => x.value).getOrElse("")
    }

    def getAdminByCookies(request: Request[AnyContent]): String = {
        request.cookies.get("auth").map(x => x.value).get.toInt match {
            case 0 => alAdminEnum.users.toString
            case 1 => alAdminEnum.admin.toString
            case 2 => alAdminEnum.admin.toString
        }
    }
    
    //授权成功
    def postSuccess = Action{
        Ok(views.html.successEmailPost())
    }
    
    //快速预约成功
//    def registerSuccess() = Action{
//        Ok(views.html.successRegister())
//    }
    def registerSuccess(name : String, email : String) = Action{
        Ok(views.html.successRegister(name, email))
    }
}