package module.business

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.pharbers.aqll.pattern.{CommonMessage, MessageDefines, ModuleTrait}
import com.pharbers.aqll.util.GetProperties
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import play.api.libs.ws.WSClient
import play.api.libs.ws.ahc.AhcWSClient

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
  * Created by qianpeng on 2017/2/13.
  */
object CallAkkaHttpModuleMessage {
    sealed class msg_CallHttp extends CommonMessage
    case class msg_CallCheckExcel(data: JsValue) extends msg_CallHttp
    case class msg_CallRunModel(data: JsValue)  extends msg_CallHttp
}
object CallAkkaHttpModule extends ModuleTrait{
    import CallAkkaHttpModuleMessage._
    import controllers.common.default_error_handler.f

    def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]]) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_CallCheckExcel(data) => checkExcel(data)
        case msg_CallRunModel(data) => runModel(data)
        case _ => println("Error---------------");???
    }

    def checkExcel(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            implicit val system = ActorSystem()
            implicit val materializer = ActorMaterializer()
            implicit val timeout = Timeout(10 second)
            val wsClient = AhcWSClient()

            val c = call(wsClient, GetProperties.Akka_Http_IP+":"+GetProperties.Akka_Http_Port+"/checkExcels", null)
                .andThen { case _ => wsClient.close() }
                .andThen { case _ => system.terminate() }
            val r = Await.result(c.mapTo[String], timeout.duration)
            if(r.equals("Ok")) {
                (Some(Map("FinalResult" -> toJson("ok"))), None)
            }else {
                (Some(Map("FinalResult" -> toJson("no"))), None)
            }
        } catch {
            case ex : Exception => (None, Some(error_handler(ex.getMessage().toInt)))
        }
    }

    def runModel(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            implicit val system = ActorSystem()
            implicit val materializer = ActorMaterializer()
            val wsClient = AhcWSClient()

            call(wsClient, GetProperties.Akka_Http_IP+":"+GetProperties.Akka_Http_Port+"/calc", null)
                .andThen { case _ => wsClient.close() }
                .andThen { case _ => system.terminate() }
            (Some(Map("FinalResult" -> toJson("is null"))), None)
        } catch {
            case ex : Exception => (None, Some(error_handler(ex.getMessage().toInt)))
        }
    }

    def call(wsClient: WSClient, uri: String, map: Map[String, String]): Future[String] = {
        wsClient.url(uri).post(map).map { response =>
            val json: JsValue = response.json
            (json \ "result").asOpt[String].getOrElse("No")
        }
    }
}
