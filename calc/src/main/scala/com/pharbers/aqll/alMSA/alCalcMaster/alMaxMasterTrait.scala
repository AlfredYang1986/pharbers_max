package com.pharbers.aqll.alMSA.alCalcMaster

import akka.actor.{Actor, ActorRef}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait._
import com.pharbers.aqll.alStart.alHttpFunc.alPanelItem

trait alMaxMasterTrait extends alCalcYMTrait with alGeneratePanelTrait
                        with alFilterExcelTrait with alSplitExcelTrait
                        with alGroupDataTrait with alCalcDataTrait
                        with alRestoreBsonTrait with alScpQueueTrait { this : Actor =>

    def preCalcYMJobs(item : alPanelItem, sender: ActorRef) ={
        pushCalcYMJobs(item, sender)
    }

    def preGeneratePanelJobs(item : alPanelItem, sender: ActorRef) ={
        pushGeneratePanelJobs(item, sender)
    }


//    def push_filter_job_impl(file: String, cp: alCalcParmary) = {
//        val act = context.actorOf(alCameoMaxDriver.props)
//        act ! push_filter_job(file, cp)
//    }
//
//    def max_calc_done_impl(mp: String Map String) = {
//        val act = context.actorOf(alCameoMaxDriver.props)
//        act ! max_calc_done(mp)
//    }
}