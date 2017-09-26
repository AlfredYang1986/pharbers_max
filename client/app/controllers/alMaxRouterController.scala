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
        (att.decrypt2JsValue(token) \ "scope").asOpt[List[String]].getOrElse(Nil) match {
            case Nil => Redirect("/index")
            case head :: tail if(head == "BD") => Redirect("/login/db")
        }
    }
    
    //验证GET URL中的功能对应跳转页面
    def validation_token(parm: String) = Action { request =>
        val token = att.decrypt2JsValue(parm)
        val temp = java.net.URLEncoder.encode(parm, "ISO-8859-1")
        val expire_in = (token \ "expire_in").asOpt[Long].map (x => x).getOrElse(throw new Exception("token parse error"))
        if (new Date().getTime > expire_in) Redirect("/expire_out")
        else
        (token \ "action").asOpt[String].getOrElse(None) match {
            case None => Redirect("/error")
            case "forget_password" => Redirect("/password/new/"+temp)
        }
    }

    //登录
    def login = Action { request =>
        Ok(views.html.login())
    }

    def codeRegistration = Action { request =>
        Ok(views.html.codeRegistration())
    }

    def infoRegistration = Action { request =>
        Ok(views.html.infoRegistration())
    }

    def dbLogin = Action { request =>
        Ok(views.html.dblogin())
    }

    def userInfoConfirm = Action { request =>
        Ok(views.html.userInfoConfirm())
    }
    //验证授权码
    def verificationRegister = Action { request =>
        Ok(views.html.register_verification())
    }
    //邮箱激活
    def emailInvocation = Action { request =>
        Ok(views.html.invocation_email())
    }
    //注册
    def register = Action {
        Ok(views.html.register())
    }
    //密码
    def findpwd = Action{
        Ok(views.html.findpwd())
    }
    def findpwd_success = Action{
        Ok(views.html.success_findpwd())
    }
    def new_pwd(token: String) = Action{
        Ok(views.html.newPassword())
    }
    //邮箱激活页面
    def inEmail = Action{
        Ok(views.html.inEmail())
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
    
    //首页2
    
    def newindex = Action { request =>
        if (getUserTokenByCookies(request).equals("")) {
            Ok(views.html.login())
        } else {
            Ok(views.html.newhome.index(getAdminByCookies(request)))
        }
    }

    //计算
    def calculaData = Action {
        Ok(views.html.CalculaData(""))
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


    //文件上传
    def filesUpload = Action { request =>
        if (getUserTokenByCookies(request).equals("")) {
            Ok(views.html.login())
        } else {
            Ok(views.html.filesUpload(getAdminByCookies(request), PageDefaultData(alModularEnum.FU, db_basic_connection, db_basic_connection)._1))
        }
    }
    
    //样本检查
    def sampleCheck = Action { request =>
        if (getUserTokenByCookies(request).equals("")) {
            Ok(views.html.login())
        } else {
            val defaultdata = PageDefaultData(alModularEnum.SC, db_basic_connection, db_basic_connection, false)
            Ok(views.html.sampleCheck(getAdminByCookies(request), defaultdata._1, defaultdata._2))
        }
    }

    //样本报告
    def samplereport = Action { request =>
        if (getUserTokenByCookies(request).equals("")) {
            Ok(views.html.login())
        } else {
            Ok(views.html.sampleReport(getAdminByCookies(request), PageDefaultData(alModularEnum.SR, db_basic_connection, db_basic_connection)._1))
        }
    }

    //结果检查
    def resultcheck = Action { request =>
        if (getUserTokenByCookies(request).equals("")) {
            Ok(views.html.login())
        } else {
            val defaultdata = PageDefaultData(alModularEnum.RC, db_basic_connection, db_basic_connection, false)
            Ok(views.html.resultCheck(getAdminByCookies(request), defaultdata._1, defaultdata._2))
        }
    }

    //结果查询
    def resultQuery = Action { request =>
        if (getUserTokenByCookies(request).equals("")) {
            Ok(views.html.login())
        } else {
            Ok(views.html.resultQuery(getAdminByCookies(request), PageDefaultData(alModularEnum.RQ, db_basic_connection, db_basic_connection)._1))
        }
    }

    //用户管理页面
    def usermanage = Action { request =>
        if (getUserTokenByCookies(request).equals("")) {
            Ok(views.html.login())
        } else {
            Ok(views.html.userManage(getAdminByCookies(request)))
        }
    }

    //市场管理页面
    def marketmanage = Action { request =>
        if (getUserTokenByCookies(request).equals("")) {
            Ok(views.html.login())
        } else {
            Ok(views.html.marketManage(getAdminByCookies(request)))
        }
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
        Ok(views.html.success_post())
    }
    
    //快速预约成功
    def registerSuccess = Action{
        Ok(views.html.success_register())
    }

    //EmberWebPage
//    def emberWebPage(path: String) = Action {
//        Ok(views.html.new_web())
//    }
}