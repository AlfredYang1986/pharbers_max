package controllers.business

import play.api._
import play.api.mvc._
import module.business.ConfigModule._

object ResultQueryController extends Controller{
  //结果查询
  def resultQuery = Action {
     Ok(views.html.resultQuery(configAllDataTypes)(configAllMarkets))
  }
}