package controllers.business

import play.api._
import play.api.mvc._

object ResultQueryController extends Controller{
  //结果查询
  def resultQuery = Action {
     Ok(views.html.resultQuery("Your new application is ready."))
  }
}