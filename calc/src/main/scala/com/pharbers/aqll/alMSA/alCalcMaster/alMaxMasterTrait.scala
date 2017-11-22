package com.pharbers.aqll.alMSA.alCalcMaster

import akka.actor.{Actor, ActorRef}
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alMaxRunning
import com.pharbers.aqll.alCalcOther.alMessgae.alWebSocket
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait._
import com.pharbers.aqll.alStart.alHttpFunc.alPanelItem
import scala.collection.immutable.Map

trait alMaxMasterTrait extends alCalcYMTrait with alGeneratePanelTrait
                        with alSplitPanelTrait with alGroupDataTrait
                        with alCalcDataTrait with alRestoreBsonTrait
                        with alScpQueueTrait { this : Actor =>

    def preCalcYMJob(item: alPanelItem, sender: ActorRef) = pushCalcYMJobs(item, sender)

    def preGeneratePanelJob(item: alPanelItem, sender: ActorRef) = pushGeneratePanelJobs(item, sender)

    def preSplitPanelJob(item: alMaxRunning, sender: ActorRef) ={
        println("split 的文件是 = " + item.panel)
        pushSplitPanelJob(item, sender)
        val msg = Map(
            "type" -> "progress_calc",
            "txt" -> "分拆文件中",
            "progress" -> "1"
        )
        alWebSocket(item.uid).post(msg)
    }

    def preGroupJob(item: alMaxRunning, sender: ActorRef) ={

        val msg = Map(
            "type" -> "progress_calc",
            "txt" -> "分拆文件中",
            "progress" -> "1"
        )
        alWebSocket(item.uid).post(msg)
    }

    def preCalcJob(item: alMaxRunning, sender: ActorRef) ={

        val msg = Map(
            "type" -> "progress_calc",
            "txt" -> "分拆文件中",
            "progress" -> "1"
        )
        alWebSocket(item.uid).post(msg)
    }
}