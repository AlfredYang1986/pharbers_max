package com.pharbers.aqll.alMSA.alMaxSlaves

import scala.concurrent.duration._
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.agent.Agent
import com.pharbers.alCalcMemory.aldata.alStorage
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alCalcParmary
import com.pharbers.aqll.alCalc.almodel.java.IntegratedData
import com.pharbers.aqll.alCalcMemory.aljobs.alJob.max_filter_csv_jobs
import com.pharbers.aqll.alCalcOther.alMessgae.{alWebSocket}
import com.pharbers.aqll.common.alString.alStringOpt.removeSpace
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger._

import scala.collection.immutable.Map

/**
  * Created by alfredyang on 11/07/2017.
  */
object alFilterExcelComeo {
    def props(file : String, cp : alCalcParmary, originSender : ActorRef, owner : ActorRef, counter : ActorRef) =
        Props(new alFilterExcelComeo(file, cp, originSender, owner, counter))
}

// TODO : should use presistence to replace normal Actor
class alFilterExcelComeo(fp : String,
                         cp : alCalcParmary,
                         originSender : ActorRef,
                         owner : ActorRef,
                         counter : ActorRef) extends Actor with ActorLogging {

    import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoFilterExcel._
    import alFilterExcelComeo._

    override def postRestart(reason: Throwable) : Unit = {
        // TODO : 计算次数，重新计算
        counter ! canIReStart(reason)
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
            println(s"&& alFilterExcelComeo.filter_excel_start_impl")
            val file = fp
            val parmary = cp

//            val cj = max_filter_excel_jobs(file)
            val cj = max_filter_csv_jobs(file)
            cj.result
            val lst = Option(cj.cur.get.storages.head.asInstanceOf[alStorage])
            lst match {
                case None => {
                    log.info("File is None")
                    self ! filter_excel_end(false, cp)
                }
                case Some(x) =>
                    x.doCalc
                    val p = x.data.asInstanceOf[List[IntegratedData]].filterNot(x => x.getYearAndmonth ==0 && !x.getMarket1Ch.isEmpty).map( x => (x.getYearAndmonth.toString.substring(0, 4), x.getMarket1Ch)).distinct
                    println(s"&& alFilterExcelComeo.filter_excel_start_impl.p.size=${p.size}")
                    x.isCalc = false
                    p.size match {
                        case 1 =>
                            parmary.year = p.head._1.toInt
                            parmary.market = removeSpace(p.head._2)
                            self ! filter_excel_end(true, parmary)
                        case n if n > 1 => {
                            log.info("需要分拆文件，再次读取")
                            self ! filter_excel_end(false, cp)
                        }
                        case ex : Int => log.info(s"Warning! filter_excel_start_impl lst match error. filter_excel_start_impl.p.size=${ex}")
                    }
            }
        }

        case canDoRestart(reason: Throwable) => super.postRestart(reason); self ! filter_excel_start_impl(fp, cp)

        case cannotRestart(reason: Throwable) => {
            val msg = Map(
                "type" -> "error",
                "error" -> s"error with actor=${self}, reason=${reason}"
            )
            alWebSocket(cp.uid).post(msg)
//            new alMessageProxy().sendMsg("100", cp.imuname, Map("error" -> s"error with actor=${self}, reason=${reason}"))
            self ! filter_excel_end(false, cp)
        }

    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val timeoutMessager = context.system.scheduler.scheduleOnce(10 minute) {
        self ! filter_excel_timeout()
    }

    def shutSlaveCameo(msg : AnyRef) = {
        originSender ! msg
        log.debug("stopping filter excel cameo")
        timeoutMessager.cancel()
        context.stop(self)
    }

}
