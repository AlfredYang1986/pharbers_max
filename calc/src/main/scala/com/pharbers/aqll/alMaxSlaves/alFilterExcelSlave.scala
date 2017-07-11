package com.pharbers.aqll.alMaxSlaves

import akka.actor.{Actor, ActorLogging, Props}
import com.pharbers.aqll.alCalc.almodel.java.IntegratedData
import com.pharbers.aqll.alCalcMaster.alMasterTrait.alCameoFilterExcel.{filter_excel_end, filter_excel_start, filter_excel_start_impl}
import com.pharbers.aqll.alCalcMemory.aldata.alStorage
import com.pharbers.aqll.alCalcMemory.aljobs.alJob.max_filter_excel_jobs
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger.push_max_job
import com.pharbers.aqll.common.alString.alStringOpt.removeSpace

/**
  * Created by alfredyang on 11/07/2017.
  */

object alFilterExcelSlave {
    def props = Props[alFilterExcelSlave]
    def name = "filter-excel-slave"
}

class alFilterExcelSlave extends Actor with ActorLogging {
    override def receive: Receive = {
        case filter_excel_start_impl(file, parmary) => {
            val cj = max_filter_excel_jobs(file)
            cj.result
            val lst = Option(cj.cur.get.storages.head.asInstanceOf[alStorage])
            lst match {
                case None => {
                    log.info("File is None")
                    sender ! filter_excel_end(false)
                }
                case Some(x) =>
                    x.doCalc
                    val p = x.data.asInstanceOf[List[IntegratedData]].filterNot(x => x.getYearAndmonth ==0 && !x.getMarket1Ch.isEmpty).map( x => (x.getYearAndmonth.toString.substring(0, 4), x.getMarket1Ch)).distinct
                    x.isCalc = false
                    p.size match {
                        case 1 =>
                            parmary.year = p.head._1.toInt
                            parmary.market = removeSpace(p.head._2)
//                            act ! push_max_job(file, parmary)
                            sender ! filter_excel_end(true)
                        case n if n > 1 => {
                            log.info("需要分拆文件，再次读取")
                            sender ! filter_excel_end(false)
                        }
                        case _ => ???
                    }
            }
        }
    }
}
