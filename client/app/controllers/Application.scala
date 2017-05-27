package controllers

import javax.inject._
import com.pharbers.aqll.dbmodule.MongoDBModule
import module.common.alModularEnum
import module.common.alAdminEnum
import module.common.alPageDefaultData._
import play.api.mvc._

@Singleton
class Application@Inject() (mdb: MongoDBModule) extends Controller {
  implicit val basic = mdb.basic
  implicit val cores = mdb.cores

  //登录
  def login = Action { request =>
      Ok(views.html.login("Your new application is ready."))
  }

  //注册
  def register = Action {
    Ok(views.html.register("Your new application is ready."))
  }

  //首页
  def index = Action { request =>
      if(getUserTokenByCookies(request).equals("")){
          Ok(views.html.login("Your new application is ready."))
      }else{
          Ok(views.html.index(getAdminByCookies(request)))
      }
  }

  //文件上传
  def filesUpload = Action { request =>
      if(getUserTokenByCookies(request).equals("")){
          Ok(views.html.login("Your new application is ready."))
      }else{
          Ok(views.html.filesUpload(getAdminByCookies(request),PageDefaultData(alModularEnum.FU,basic,cores)._1))
      }
  }

  //样本检查
  def sampleCheck = Action { request =>
      if(getUserTokenByCookies(request).equals("")){
          Ok(views.html.login("Your new application is ready."))
      }else{
          val defaultdata = PageDefaultData(alModularEnum.SC,basic,cores,false)
          Ok(views.html.sampleCheck(getAdminByCookies(request),defaultdata._1,defaultdata._2))
      }
  }

  //样本报告
  def samplereport = Action { request =>
    if(getUserTokenByCookies(request).equals("")){
      Ok(views.html.login("Your new application is ready."))
    }else{
      Ok(views.html.sampleReport(getAdminByCookies(request),PageDefaultData(alModularEnum.SR,basic,cores)._1))
    }
  }

  //结果检查
  def resultcheck = Action { request =>
      if(getUserTokenByCookies(request).equals("")){
          Ok(views.html.login("Your new application is ready."))
      }else{
        val defaultdata = PageDefaultData(alModularEnum.RC,basic,cores,false)
        Ok(views.html.resultCheck(getAdminByCookies(request),defaultdata._1,defaultdata._2))
      }
  }

  //结果查询
  def resultQuery = Action { request =>
      if(getUserTokenByCookies(request).equals("")){
          Ok(views.html.login("Your new application is ready."))
      }else{
          Ok(views.html.resultQuery(getAdminByCookies(request),PageDefaultData(alModularEnum.RQ,basic,cores)._1))
      }
  }

  //用户管理页面
  def usermanage = Action {request =>
    if(getUserTokenByCookies(request).equals("")){
      Ok(views.html.login("Your new application is ready."))
    }else{
      Ok(views.html.userManage(getAdminByCookies(request)))
    }
  }

  //市场管理页面
  def marketmanage = Action {request =>
    if(getUserTokenByCookies(request).equals("")){
      Ok(views.html.login("Your new application is ready."))
    }else{
      Ok(views.html.marketManage(getAdminByCookies(request)))
    }
  }

  def getUserTokenByCookies(request: Request[AnyContent]) : String = {
    request.cookies.get("user_token").map (x => x.value).getOrElse("")
  }

  def getAdminByCookies(request: Request[AnyContent]) : String = {
    request.cookies.get("is_administrator").map(x => x.value).get.toInt match {
      case 0 => alAdminEnum.users.toString
      case 1 => alAdminEnum.admin.toString
      case 2 => alAdminEnum.admin.toString
    }
  }

  //EmberWebPage
  def emberWebPage(path : String) = Action {
    Ok(views.html.new_web())
  }
}