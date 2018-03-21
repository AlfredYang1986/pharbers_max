package com.pharbers.aqll.alMSA.alMaxSlaves

import com.pharbers.aqll.alCalcHelp.alLog.alTempLog
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.reStartMsg._

/**
  * Created by jeorch on 17-8-10.
  */
object alCommonErrorCounter {
    def props = Props[alCommonErrorCounter]
}

class alCommonErrorCounter extends Actor with ActorLogging {
    var count = 3

    override def receive = {
        case canIReStart(reason) => validateCount(reason, sender)
    }

    def validateCount(reason: Throwable, sender: ActorRef) = {
        count -= 1
        log.info(s"errorCounter ==> 第${3-count}次重新尝试, error with sender=$sender, reason=$reason##")
        count match {
            case 0 => {
                log.info(s"errorCounter ==> 在尝试3次后，其中的某个线程计算失败，正在结束停止计算！ error with sender=$sender, reason=$reason##")
                alTempLog(s"errorCounter ==> 在尝试3次后，其中的某个线程计算失败，正在结束停止计算！ error with sender=$sender, reason=$reason##")
                sender ! cannotRestart(reason)
            }

            case x if x > 0 => sender ! canDoRestart(reason)

            case _ => {
                log.info("Validate reCalculate countNumber Error! Illegal Count!")
                alTempLog("Validate reCalculate countNumber Error! Illegal Count!")
                sender ! cannotRestart(reason)
            }
        }
    }

}
