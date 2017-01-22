package scala.com.pharbers.aqll.calc.Http

import java.io.File

import scala.concurrent.ExecutionContext
import akka.http.scaladsl.server.Directives._

import scala.concurrent.duration._
import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import akka.pattern.ask
import com.pharbers.aqll.calc.Http.{QueueActor, ThreadQueue}

import scala.concurrent.Await
import com.pharbers.aqll.calc.maxmessages.excelJobStart
import com.pharbers.aqll.calc.split.JobCategories._
import com.pharbers.aqll.calc.split.{ClusterEventListener, SplitReception}
import com.pharbers.aqll.calc.util.{GetProperties, ListQueue}

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

    val routes = getCheck ~ getCalc ~ Test ~ Test2

    def Test = get {
        path("Test") {
            val ref: AnyRef = excelJobStart("""config/test/BMS客户上传/201601-07-CPA-Baraclude产品待上传.xlsx""", cpaProductJob, "BMS", 0)
            ListQueue.ListMq_Queue(ref)
            complete("""jsonpCallback1({"result":"Ok"})""")
        }
    }

    def Test2 = get {
        path("Test2") {
            val ref: AnyRef = excelJobStart("""config/test/BMS客户上传/201601-07-CPA-Baraclude产品待上传.xlsx""", cpaProductJob, "BMS2", 0)
            ListQueue.ListMq_Queue(ref)
            complete("""jsonpCallback1({"result":"Ok"})""")
        }
    }

    def getCheck = get {
        path("checkExcel") {
            parameters(("filename", "company", "filetype")) { (filename, company, filetype) =>
                val system = ActorSystem(filename)
                val act = system.actorOf(CheckReception.props)
                val r = filetype match  {
                    case "0" => {
                        act ? excelJobStart(GetProperties.loadProperties("File.properties").getProperty("Upload_File_Path").toString + filename, cpaProductJob, company, 0)
                    }
                    case "1" => {
                        act ? excelJobStart(GetProperties.loadProperties("File.properties").getProperty("Upload_File_Path").toString + filename, cpaMarketJob, company, 0)
                    }
                    case "2" => {
                        act ? excelJobStart(GetProperties.loadProperties("File.properties").getProperty("Upload_File_Path").toString + filename, phaProductJob, company, 0)
                    }
                    case "3" => {
                        act ? excelJobStart(GetProperties.loadProperties("File.properties").getProperty("Upload_File_Path").toString + filename, phaMarketJob, company, 0)
                    }
                }
                val result = Await.result(r.mapTo[String], requestTimeout.duration)
                if(result.equals("is ok")){
                    system.terminate()
                    System.gc()
                    complete("""jsonpCallback1({"result":"Ok"})""")
                }else{
                    system.terminate()
                    System.gc()
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
                        calc ! excelJobStart(GetProperties.loadProperties("File.properties").getProperty("Upload_File_Path").toString + filename, cpaProductJob, company, 0)
                    }
                    case "1" => {
                        calc ! excelJobStart(GetProperties.loadProperties("File.properties").getProperty("Upload_File_Path").toString + filename, cpaMarketJob, company, 0)
                    }
                    case "2" => {
                        calc ! excelJobStart(GetProperties.loadProperties("File.properties").getProperty("Upload_File_Path").toString + filename, phaProductJob, company, 0)
                    }
                    case "3" => {
                        calc ! excelJobStart(GetProperties.loadProperties("File.properties").getProperty("Upload_File_Path").toString + filename, phaMarketJob, company, 0)
                    }
                }
                complete("""jsonpCallback1({"result":"Ok"})""")
            }
        }
    }
}
