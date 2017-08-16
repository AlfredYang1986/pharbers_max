package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.actor.SupervisorStrategy.Restart

import scala.concurrent.duration._
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props, SupervisorStrategy}
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alCalcParmary
import com.pharbers.aqll.alCalc.almodel.java.IntegratedData
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoFilterExcel.{filter_excel_end, filter_excel_start_impl, filter_excel_timeout}
import com.pharbers.aqll.alCalcMemory.aldata.alStorage
import com.pharbers.aqll.alCalcMemory.aljobs.alJob.max_filter_excel_jobs
import com.pharbers.aqll.alCalcOther.alMessgae.alMessageProxy
import com.pharbers.aqll.common.alString.alStringOpt.removeSpace

/**
  * Created by alfredyang on 11/07/2017.
  */
object alFilterExcelComeo {
    def props(file : String, cp : alCalcParmary, originSender : ActorRef, owner : ActorRef) =
        Props(new alFilterExcelComeo(file, cp, originSender, owner))
    var count = 3
}

// TODO : should use presistence to replace normal Actor
class alFilterExcelComeo(fp : String,
                         cp : alCalcParmary,
                         originSender : ActorRef,
                         owner : ActorRef) extends Actor with ActorLogging {

    import alFilterExcelComeo._
    import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoFilterExcel._

    override def postRestart(reason: Throwable) : Unit = {
        // TODO : 计算次数，重新计算
        count -= 1
        count match {
            case 0 => new alMessageProxy().sendMsg("100", "username", Map("error" -> "alFilterExcelComeo error"))
                self ! filter_excel_end(false)
            case _ => super.postRestart(reason); self ! filter_excel_start_impl(fp, cp)
        }
    }

    override def receive: Receive = {
        case filter_excel_timeout() => {
            log.debug("timeout occur")
            shutSlaveCameo(filter_excel_timeout())
        }
        // TODO: 内存泄漏，稳定后修改
        case result : filter_excel_end => {
            owner forward result
            shutSlaveCameo(result)
        }
        case _ : filter_excel_start_impl => {
            val file = fp
            val parmary = cp

            val cj = max_filter_excel_jobs(file)
            cj.result
            val lst = Option(cj.cur.get.storages.head.asInstanceOf[alStorage])
            lst match {
                case None => {
                    log.info("File is None")
                    sender ! filter_excel_end(false, file, parmary)
                }
                case Some(x) =>
                    x.doCalc
                    val p = x.data.asInstanceOf[List[IntegratedData]].filterNot(x => x.getYearAndmonth ==0 && !x.getMarket1Ch.isEmpty).map( x => (x.getYearAndmonth.toString.substring(0, 4), x.getMarket1Ch)).distinct
                    x.isCalc = false
                    p.size match {
                        case 1 =>
                            parmary.year = p.head._1.toInt
                            parmary.market = removeSpace(p.head._2)
                            // TODO: 内存泄漏，稳定后修改
//                            self ! filter_excel_end(true)
                            sender ! filter_excel_end(true, file, parmary)
                        case n if n > 1 => {
                            log.info("需要分拆文件，再次读取")
                            sender ! filter_excel_end(false, file, parmary)
                            stateAgent send state_agent(false)
                            sender ! filter_excel_end(true)
                        case n if n > 1 => {
                            log.info("需要分拆文件，再次读取")
                            stateAgent send state_agent(false)
                            sender ! filter_excel_end(false)
                        }
                        case _ => ???
                    }
            }
        }
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val timeoutMessager = context.system.scheduler.scheduleOnce(10 minute) {
        self ! filter_excel_timeout()
    }

    def shutSlaveCameo(msg : AnyRef) = {
        originSender ! msg
        log.debug("stopping filter excel cameo")
        context.stop(self)
    }
}
