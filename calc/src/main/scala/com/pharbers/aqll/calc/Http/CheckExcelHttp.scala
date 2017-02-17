package com.pharbers.aqll.calc.Http

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
import com.pharbers.aqll.calc.split.JobCategories.{integratedJob, _}
import com.pharbers.aqll.calc.split.{ClusterEventListener, SplitReception}
import com.pharbers.aqll.calc.util.{GetProperties, ListQueue}
import com.pharbers.aqll.calc.check.CheckReception



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
            val map = Map("filename" -> """config/test/BMS客户上传/CPA_GYCX_panel_160111INF.xlsx""",
                          "hospdatapath" -> """20000家pfizer医院数据库表.xlsx""",
                          "JobDefines" -> integratedJob,
                          "company" -> "BMS",
                          "calcvariable" -> 0)
            val ref: AnyRef = excelJobStart(map)
            ListQueue.ListMq_Queue(ref)
            complete("""jsonpCallback1({"result":"Ok"})""")
        }
    }

    def getCheck = get {
        path("checkExcel") {
            parameters(("filename", "company", "filetype")) { (filename, company, filetype) =>
              val map = Map("filename" -> (GetProperties.loadConf("File.conf").getString("SCP.Upload_File_Path").toString + filename),
                            "hospdatapath" -> """20000家pfizer医院数据库表.xlsx""",
                            "JobDefines" -> integratedJob,
                            "company" -> company,
                            "calcvariable" -> 0)
                val system = ActorSystem(filename)
                val act = system.actorOf(CheckReception.props)
                val r = filetype match  {
//                    case "0" => {
//                        act ? excelJobStart(, cpaProductJob, company, 0)
//                    }
//                    case "1" => {
//                        act ? excelJobStart(GetProperties.loadConf("File.conf").getString("SCP.Upload_File_Path").toString + filename, cpaMarketJob, company, 0)
//                    }
//                    case "2" => {
//                        act ? excelJobStart(GetProperties.loadConf("File.conf").getString("SCP.Upload_File_Path").toString + filename, phaProductJob, company, 0)
//                    }
//                    case "3" => {
//                        act ? excelJobStart(GetProperties.loadConf("File.conf").getString("SCP.Upload_File_Path").toString + filename, phaMarketJob, company, 0)
//                    }
                    case "4" => {
                        act ? excelJobStart(map)
                    }
                }
                val result = Await.result(r.mapTo[String], requestTimeout.duration)
                if(result.equals("is ok")){
                    system.terminate()
                    System.gc()
                    complete("""{"result":"Ok"}""")
                }else{
                    system.terminate()
                    System.gc()
                    complete("""{"result":"No"}""")
                }
//                complete("""{"result":"Ok"}""")
            }
        }
    }

    def getCalc = get {
        path("calc") {
            parameters(("filename", "company", "filetype")) { (filename, company, filetype) =>
              val map = Map("filename" -> (GetProperties.loadConf("File.conf").getString("SCP.Upload_File_Path").toString + filename),
                            "hospdatapath" -> """20000家pfizer医院数据库表.xlsx""",
                            "JobDefines" -> integratedJob,
                            "company" -> company,
                            "calcvariable" -> 0)
                val system = ActorSystem("calc")
                val calc = system.actorOf(Props[SplitReception], "splitreception")
                filetype match  {
//                    case "0" => {
//                        calc ! excelJobStart(GetProperties.loadConf("File.conf").getString("SCP.Upload_File_Path").toString + filename, cpaProductJob, company, 0)
//                    }
//                    case "1" => {
//                        calc ! excelJobStart(GetProperties.loadConf("File.conf").getString("SCP.Upload_File_Path").toString + filename, cpaMarketJob, company, 0)
//                    }
//                    case "2" => {
//                        calc ! excelJobStart(GetProperties.loadConf("File.conf").getString("SCP.Upload_File_Path").toString + filename, phaProductJob, company, 0)
//                    }
//                    case "3" => {
//                        calc ! excelJobStart(GetProperties.loadConf("File.conf").getString("SCP.Upload_File_Path").toString + filename, phaMarketJob, company, 0)
//                    }
                  case "4" => {
                      calc ! excelJobStart(map)
                  }
                }
                complete("""({"result":"Ok"})""")
            }
        }
    }
}
