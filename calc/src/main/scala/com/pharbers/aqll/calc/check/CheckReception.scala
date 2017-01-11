package scala.com.pharbers.aqll.calc.check

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}
import com.pharbers.aqll.calc.maxmessages.{checkResult, excelJobEnd, excelJobStart, startReadExcel}

/**
  * Created by Faiz on 2017/1/4.
  */

object CheckReception {
    def props = Props[CheckReception]
}

class CheckReception extends Actor with ActorLogging{
    var originSender : ActorRef = null
    def receive = {
        case excelJobStart(filename, cat, company, n) => {
            originSender = sender()
            println("in excelJobStart")
            val act = context.actorOf(CheckMaster.props(self))
            context.watch(act)
            act ! startReadExcel(filename, cat, company, n)
        }
        case excelJobEnd(filename) => {
            println(filename)
        }
        case checkResult(msg) => {
            originSender ! msg
            println(s"originSender = $originSender")
        }
        case Terminated(a) => {
            println("-*-*-*-*-*-*-*-")
            context.stop(self)
            context.unwatch(a)
            // job完成，提醒用户
        }
        case str => println(str)
        case _ => ???
    }
}

trait CreateCheckMaster { this : Actor =>
    def CreateCheckMaster = context.actorOf(CheckReception.props)
}