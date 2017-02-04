package com.pharbers.aqll.actor

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.util.Timeout
import play.api.libs.concurrent.Akka

import scala.concurrent.duration._
import akka.pattern.ask
import com.pharbers.aqll.pattern.JobCategories.cpaProductJob
import com.pharbers.aqll.pattern.excelJobStart
import com.typesafe.config.ConfigFactory
import play.api.Play.current

import scala.concurrent.Await
case class checkitem()
/**
  * Created by Faiz on 2017/1/5.
  */
object CheckExcelActor {
    def props(company: String) = Props(new CheckExcelActor(company))
//    lazy val check = Akka.system.actorOf(Props[CheckExcel], "CheckExcel")
}

class CheckExcelActor(company: String) extends Actor with ActorLogging{
    implicit val timeout = Timeout(180 second)
    val check: Receive = {
        case checkitem() => {
            println(context.system)
            println(Akka.system)
            println(sender)
            println(self)
            val path = ConfigFactory.load().getString("bazar.remotepath")
            val remote = Akka.system.actorSelection(path)
            println(context.system.settings.config)
            println(remote)
            val r = remote ? excelJobStart("""config/test/BMS客户上传/201601-07-CPA-Baraclude产品待上传.xlsx""", cpaProductJob, company, 0)
            val result = Await.result(r.mapTo[String], timeout.duration)
            println(result)
            sender ! result
        }

        case _ => ???
    }
    def receive = check
}
