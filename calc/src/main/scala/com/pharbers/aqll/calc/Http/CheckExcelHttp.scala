package scala.com.pharbers.aqll.calc.Http

import java.io.File

import scala.concurrent.ExecutionContext
import akka.http.scaladsl.server.Directives._

import scala.concurrent.duration._
import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import akka.pattern.ask

import scala.concurrent.Await
import com.pharbers.aqll.calc.maxmessages.excelJobStart
import com.pharbers.aqll.calc.split.JobCategories._
import com.pharbers.aqll.calc.split.SplitReception

import scala.com.pharbers.aqll.calc.check.CheckReception



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

    val routes = getCheck ~ getCalc ~ Test

    def Test = get {
        path("Test") {
            complete("""jsonpCallback1({"result":"Ok"})""")
        }
    }

    def getCheck = get {
        path("checkExcel") {
            parameters(("filename", "company", "filetype")) { (filename, company, filetype) =>
                val system = ActorSystem("excelCall")
                val act = system.actorOf(CheckReception.props)
                val r = filetype match  {
                    case "0" => {
                        act ? excelJobStart("""D:\SourceData\Client\"""+filename, cpaProductJob, company, 0)
                    }
                    case "1" => {
                        act ? excelJobStart("""D:\SourceData\Client\"""+filename, cpaMarketJob, company, 0)
                    }
                    case "2" => {
                        act ? excelJobStart("""D:\SourceData\Client\"""+filename, phaProductJob, company, 0)
                    }
                    case "3" => {
                        act ? excelJobStart("""D:\SourceData\Client\"""+filename, phaMarketJob, company, 0)
                    }
                }
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

    def getCalc = get {
        path("calc") {
            parameters(("filename", "company", "filetype")) { (filename, company, filetype) =>
                val system = ActorSystem("calc")
                val calc = system.actorOf(Props[SplitReception], "splitreception")
                filetype match  {
                    case "0" => {
                        calc ! excelJobStart("""D:\SourceData\Client\"""+filename, cpaProductJob, company, 0)
                    }
                    case "1" => {
                        calc ! excelJobStart("""D:\SourceData\Client\"""+filename, cpaMarketJob, company, 0)
                    }
                    case "2" => {
                        calc ! excelJobStart("""D:\SourceData\Client\"""+filename, phaProductJob, company, 0)
                    }
                    case "3" => {
                        calc ! excelJobStart("""D:\SourceData\Client\"""+filename, phaMarketJob, company, 0)
                    }
                }
                complete("""jsonpCallback1({"result":"Ok"})""")
            }
        }
    }
}
