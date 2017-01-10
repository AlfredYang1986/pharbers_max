package scala.com.pharbers.aqll.calc.check

import scala.concurrent.ExecutionContext
import akka.http.scaladsl.server.Directives._

import scala.concurrent.duration._
import akka.actor.ActorSystem
import akka.util.Timeout
import akka.pattern.ask

import scala.concurrent.Await
import com.pharbers.aqll.calc.maxmessages.excelJobStart
import com.pharbers.aqll.calc.split.JobCategories.cpaProductJob



/**
  * Created by Faiz on 2017/1/7.
  */


class OrderServiceApi(system: ActorSystem, timeout: Timeout) extends OrderService {
    implicit val requestTimeout = timeout
    implicit def executionContext = system.dispatcher
}

trait OrderService {
    implicit def executionContext: ExecutionContext

    implicit def requestTimeout: Timeout

    val routes = getOrder



    def getOrder = get {
        path("checkExcel") {
            parameters(("filename","company")) { (filename, company) =>
                println(s"filename = $filename")
                println(s"company = $company")
                val system = ActorSystem("excelCall")
                val act = system.actorOf(CheckReception.props)
                val r = act ? excelJobStart("""config/test/BMS客户上传/201601-07-CPA-Baraclude产品待上传.xlsx""", cpaProductJob, "098f6bcd4621d373cade4e832627b4f6", 0)
                val result = Await.result(r.mapTo[String], requestTimeout.duration)
                if(result.equals("is ok")){
                    system.terminate()
                    complete("""jsonpCallback1({"result":"Ok"})""")
                }else{
                    system.terminate()
                    complete("""jsonpCallback1({"result":"No"})""")
                }

            }
        }
    }
}
