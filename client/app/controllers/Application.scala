package controllers

import play.api._
import javax.inject._

import module.common.MarketsModule
import play.api.mvc._

@Singleton
class Application extends Controller {
  def test = Action {
      Ok(views.html.test("Your new application is ready."))
  }

  //登录
  def login = Action { request =>
      Ok(views.html.login("Your new application is ready."))
  }

  //首页
  def index = Action { request =>
      val token = request.cookies.get("user_token").map (x => x.value).getOrElse("")
      if(token.equals("")){
          Ok(views.html.login("Your new application is ready."))
      }else{
          Ok(views.html.index(enumAdministrator(request.cookies.get("is_administrator").map(x => x.value).get.toInt)))
      }
  }

  //注册
  def register = Action {
      Ok(views.html.register("Your new application is ready."))
  }

  //错误404
  def error404 = Action {
      Ok(views.html.error404("Your new application is ready."))
  }

  //错误500
  def error500 = Action {
      Ok(views.html.error500("Your new application is ready."))
  }

  //锁屏
  def lockScreen = Action {
      Ok(views.html.lockScreen("Your new application is ready."))
  }

  //忘记密码
  def forgotPassword = Action {
      Ok(views.html.forgotPassword("Your new application is ready."))
  }

  //空页面
  def emptyPage = Action {
      Ok(views.html.emptyPage("Your new application is ready."))
  }

  //文件上传
  def filesUpload = Action { request =>
      val token = request.cookies.get("user_token").map (x => x.value).getOrElse("")
      if(token.equals("")){
          Ok(views.html.login("Your new application is ready."))
      }else{
          Ok(views.html.filesUpload(enumAdministrator(request.cookies.get("is_administrator").map(x => x.value).get.toInt)))
      }
  }

  //样本检查
  def sampleCheck = Action { request =>
      val token = request.cookies.get("user_token").map (x => x.value).getOrElse("")
      if(token.equals("")){
          Ok(views.html.login("Your new application is ready."))
      }else{
          Ok(views.html.sampleCheck(enumAdministrator(request.cookies.get("is_administrator").map(x => x.value).get.toInt),MarketsModule.pushMarkets))
      }
  }

  //模型运算
  def modelOperation = Action { request =>
      val token = request.cookies.get("user_token").map (x => x.value).getOrElse("")
      if(token.equals("")){
          Ok(views.html.login("Your new application is ready."))
      }else{
          Ok(views.html.modelOperation(enumAdministrator(request.cookies.get("is_administrator").map(x => x.value).get.toInt),MarketsModule.pushMarkets))
      }
  }

  //结果查询
  def resultQuery = Action { request =>
      val token = request.cookies.get("user_token").map (x => x.value).getOrElse("")
      if(token.equals("")){
          Ok(views.html.login("Your new application is ready."))
      }else{
          Ok(views.html.resultQuery(enumAdministrator(request.cookies.get("is_administrator").map(x => x.value).get.toInt),MarketsModule.pushMarkets))
      }
  }

  //管理员
  def manageUploadFile = Action { request =>
      val token = request.cookies.get("user_token").map (x => x.value).getOrElse("")
      val is_administrator = enumAdministrator(request.cookies.get("is_administrator").map(x => x.value).get.toInt)
      if(token.equals("") || is_administrator.equals("No")){
          Ok(views.html.login("Your new application is ready."))
      }else{
          Ok(views.html.manageUpload(is_administrator,MarketsModule.pushMarkets))
      }
  }

  def enumAdministrator(is_administrator : Int) = is_administrator match {
        case 0 => "No"
        case 1 => "Yes"
  }
}