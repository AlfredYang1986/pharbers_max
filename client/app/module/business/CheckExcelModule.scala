package module.business

import akka.actor.ActorSystem

import scala.concurrent.duration._
import akka.util.Timeout
import akka.pattern.ask
import com.pharbers.aqll.actor.{CheckExcelActor, checkitem}

import scala.concurrent.Await
import com.pharbers.aqll.pattern.JobCategories.cpaProductJob
import com.pharbers.aqll.pattern.{CommonMessage, MessageDefines, ModuleTrait, excelJobStart}
import com.pharbers.aqll.util.GetProperties
import com.typesafe.config.ConfigFactory
import play.api.libs.concurrent.Akka
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import play.api.Play.current

/**
  * Created by Faiz on 2017/1/5.
  */

object CheckExcelModuleMessage {
    sealed class msg_CheckExcelBaseQuery extends CommonMessage
    case class msg_checkexcel(data: JsValue) extends msg_CheckExcelBaseQuery
}

object CheckExcelModule extends ModuleTrait{
    import CheckExcelModuleMessage._
    import controllers.common.default_error_handler.f

    def dispatchMsg(msg: MessageDefines)(pr : Option[Map[String, JsValue]]) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_checkexcel(data) => checkexcel(data)
        case _ => println("Error---------------");???
    }

    def checkexcel(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        implicit val timeout = Timeout(180 second)
        val filename = (data \ "filename").asOpt[String].get
        val company = (data \ "company").asOpt[String].get
        val fullfile = GetProperties.loadProperties("File.properties").getProperty("Upload_File_Path")+filename
        try {
//            val config = ConfigFactory.load("akkaconf/check-remote")
//            val path = "akka.tcp://ExcelMain@127.0.0.1:4771/user/sample"
//            val system = Akka.system //ActorSystem("checkexcel", config)
//            val sample = system.actorSelection(path)
//            val r = sample ? excelJobStart("""config/test/BMS客户上传/201601-07-CPA-Baraclude产品待上传.xlsx""", cpaProductJob, company, 0)
//            val rst = Await.result(r.mapTo[String], timeout.duration)
//            println(rst)
            val act = Akka.system.actorOf(CheckExcelActor.props(company))
            val r = act ? checkitem()
            val rst = Await.result(r.mapTo[String], timeout.duration)
            println(rst)
            if(rst.equals("is ok")) {
                //system.shutdown()
                (Some(Map("FinalResult" -> toJson("ok"))), None)
            }else{
                (Some(Map("FinalResult" -> toJson("no"))), None)
            }
        } catch {
            case ex : Exception => (None, Some(error_handler(ex.getMessage().toInt)))
        }
    }


}
