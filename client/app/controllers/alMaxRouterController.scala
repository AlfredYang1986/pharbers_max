package controllers

import javax.inject._

import akka.actor.ActorSystem
import com.pharbers.aqll.common.{alAdminEnum, alModularEnum}
import com.pharbers.cliTraits.DBTrait
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.mongodbConnect.connection_instance
import com.pharbers.token.AuthTokenTrait
import module.common.alPageDefaultData._
import play.api.mvc._

class alMaxRouterController @Inject() (as_inject : ActorSystem, dbc : dbInstanceManager, att : AuthTokenTrait) extends Controller {
    implicit val as = as_inject
    implicit val db_cores : DBTrait = dbc.queryDBInstance("calc").get
    implicit val db_basic : DBTrait = dbc.queryDBInstance("cli").get

    implicit val db_cores_connection : connection_instance = dbc.queryDBConnection("calc").get
    implicit val db_basic_connection : connection_instance = dbc.queryDBConnection("cli").get
    
    

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

    //注册
    def register = Action {
        Ok(views.html.register())
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
        Ok(views.html.newhome.calcData(getAdminByCookies(request)))
    }

    //历史数据
    def historyData = Action { request =>
        Ok(views.html.HistoryData(getAdminByCookies(request), PageDefaultData(alModularEnum.RQ, db_basic_connection, db_basic_connection)._1))
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