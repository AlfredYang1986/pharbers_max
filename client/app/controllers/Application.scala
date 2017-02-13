package controllers

import play.api._
import javax.inject._
import play.api.mvc._
import module.business.MarketsModule

@Singleton
class Application extends Controller {
  //官网
  def landing = Action {
      Ok(views.html.landing("Your new application is ready."))
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
          Ok(views.html.index("Your new application is ready."))
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
          Ok(views.html.filesUpload("Your new application is ready."))
      }
  }

  //样本检查
  def sampleCheck = Action { request =>
      val token = request.cookies.get("user_token").map (x => x.value).getOrElse("")
      if(token.equals("")){
          Ok(views.html.login("Your new application is ready."))
      }else{
          Ok(views.html.sampleCheck("Your new application is ready."))
      }
  }

  //模型运算
  def modelOperation = Action { request =>
      val token = request.cookies.get("user_token").map (x => x.value).getOrElse("")
      if(token.equals("")){
          Ok(views.html.login("Your new application is ready."))
      }else{
          Ok(views.html.modelOperation("Your new application is ready."))
      }
  }

  //结果检查
  def resultCheck = Action { request =>
      val token = request.cookies.get("user_token").map (x => x.value).getOrElse("")
      if(token.equals("")){
          Ok(views.html.login("Your new application is ready."))
      }else{
          Ok(views.html.resultCheck("Your new application is ready."))
      }
  }


  //结果查询
  def resultQuery = Action { request =>
      val token = request.cookies.get("user_token").map (x => x.value).getOrElse("")
      if(token.equals("")){
          Ok(views.html.login("Your new application is ready."))
      }else{
          Ok(views.html.resultQuery(MarketsModule.pushMarkets(token)))
      }
  }

  //结果查询
  def manageUploadFile = Action { request =>
    val token = request.cookies.get("user_token").map (x => x.value).getOrElse("")
    if(token.equals("")){
      Ok(views.html.login("Your new application is ready."))
    }else{
      Ok(views.html.manageUpload("Your new application is ready."))
    }
  }
}