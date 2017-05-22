package controllers

import javax.inject._

import com.pharbers.aqll.dbmodule.MongoDBModule
import com.pharbers.aqll.pattern.CommonModule
import module.common.alMarkets
import play.api.mvc._

@Singleton
class Application@Inject() (mdb: MongoDBModule) extends Controller {
  implicit val basic = mdb.basic
  implicit val cores = mdb.cores

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

  //文件上传
  def filesUpload = Action { request =>
      val token = request.cookies.get("user_token").map (x => x.value).getOrElse("")
      val is_administrator = enumAdministrator(request.cookies.get("is_administrator").map(x => x.value).get.toInt)
      if(token.equals("")){
          Ok(views.html.login("Your new application is ready."))
      }else{
          Ok(views.html.filesUpload(is_administrator,alMarkets.alGetMarkets("",basic,cores)))
      }
  }

  //样本检查
  def sampleCheck = Action { request =>
      val token = request.cookies.get("user_token").map (x => x.value).getOrElse("")
      if(token.equals("")){
          Ok(views.html.login("Your new application is ready."))
      }else{
          Ok(views.html.sampleCheck(enumAdministrator(request.cookies.get("is_administrator").map(x => x.value).get.toInt),alMarkets.alGetMarkets("sc",basic,cores)))
      }
  }

  //样本报告
  def samplereport = Action { request =>
    val token = request.cookies.get("user_token").map (x => x.value).getOrElse("")
    if(token.equals("")){
      Ok(views.html.login("Your new application is ready."))
    }else{
      Ok(views.html.sampleReport(enumAdministrator(request.cookies.get("is_administrator").map(x => x.value).get.toInt),alMarkets.alGetMarkets("sc",basic,cores)))
    }
  }

  //结果检查
  def resultcheck = Action { request =>
      val token = request.cookies.get("user_token").map (x => x.value).getOrElse("")
      if(token.equals("")){
          Ok(views.html.login("Your new application is ready."))
      }else{
          Ok(views.html.resultCheck(enumAdministrator(request.cookies.get("is_administrator").map(x => x.value).get.toInt),alMarkets.alGetMarkets("",basic,cores)))
      }
  }

  //结果查询
  def resultQuery = Action { request =>
      val token = request.cookies.get("user_token").map (x => x.value).getOrElse("")
      if(token.equals("")){
          Ok(views.html.login("Your new application is ready."))
      }else{
          Ok(views.html.resultQuery(enumAdministrator(request.cookies.get("is_administrator").map(x => x.value).get.toInt),alMarkets.alGetMarkets("",basic,cores)))
      }
  }

  //用户管理页面
  def usermanage = Action {request =>
    val token = request.cookies.get("user_token").map (x => x.value).getOrElse("")
    val is_administrator = enumAdministrator(request.cookies.get("is_administrator").map(x => x.value).get.toInt)
    if(token.equals("")){
      Ok(views.html.login("Your new application is ready."))
    }else{
      Ok(views.html.userManage(is_administrator))
    }
  }

  //市场管理页面
  def marketmanage = Action {request =>
    val token = request.cookies.get("user_token").map (x => x.value).getOrElse("")
    val is_administrator = enumAdministrator(request.cookies.get("is_administrator").map(x => x.value).get.toInt)
    if(token.equals("")){
      Ok(views.html.login("Your new application is ready."))
    }else{
      Ok(views.html.marketManage(is_administrator))
    }
  }

  def enumAdministrator(is_administrator : Int) = is_administrator match {
        case 0 => "No"
        case 1 => "Yes"
  }
}