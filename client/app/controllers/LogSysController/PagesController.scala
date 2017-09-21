package controllers.LogSysController

import play.api.mvc._

/**
  * Created by yym on 9/4/17.
  */
class PagesController extends Controller{
//    def gomaxService = Action{
//        Ok(views.html.maxService())
//    }
//
//    def gomaxServiceDetail = Action{
//        Ok(views.html.maxServiceDetail())
//    }
    
        def postSuccess = Action{
            Ok(views.html.success_post())
        }
}
