package com.pharbers.aqll.alMSA.alCalcMaster

import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.groupMsg.pushGroupJob
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.scpMsg.pushScpJob
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.calcMsg.pushCalcJob
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.restoreMsg.pushRestoreJob

import java.util.UUID
import akka.actor.Actor
import com.redis.RedisClient
import play.api.libs.json.JsValue
import scala.collection.immutable.Map
import com.pharbers.driver.redis.phRedisDriver
import com.pharbers.aqll.alCalcHelp.alLog.alTempLog
import com.pharbers.aqll.alStart.alHttpFunc.alPanelItem
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait._
import com.pharbers.aqll.alCalcHelp.alWebSocket.alWebSocket
import com.pharbers.aqll.alCalcHelp.alMaxDefines.alMaxRunning
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.refundNodeForRole

trait alMaxMasterTrait extends alCalcYMTrait with alGeneratePanelTrait
                        with alSplitPanelTrait with alGroupDataTrait with alScpQueueTrait
                        with alCalcDataTrait with alRestoreBsonTrait{ this : Actor =>
    val rd: RedisClient =  phRedisDriver().commonDriver

    def preCalcYMJob(item: alPanelItem): Unit = {
        pushCalcYMJobs(item)
    }

    def postCalcYMJob(uid: String, ym: String, mkt: String): Unit = {
        alTempLog(s"calcYM result, ym = $ym, mkt = $mkt")

        val success = !(ym.isEmpty || mkt.isEmpty)
        if(success){
            val msg = Map(
                "type" -> "calc_ym_result",
                "ym" -> ym,
                "mkt" -> mkt
            )
            alWebSocket(uid).post(msg)
        }

        val a = context.actorSelection("akka.tcp://calc@" + masterIP + ":2551/user/agent-reception")
        a ! refundNodeForRole("splitcalcymslave")
    }

    def preGeneratePanelJob(item: alPanelItem): Unit = {
        val rid = UUID.randomUUID().toString
        alTempLog("开始生成panel，本次计算流程的rid为 = " + rid)
        rd.hset(item.uid, "company", item.company)
        rd.hset(item.uid, "rid", rid)
        pushGeneratePanelJobs(item)
    }

    private def jv2map(data: JsValue): Map[String, Map[String, List[String]]] ={
        data.as[Map[String, JsValue]].map{ x =>
            x._1 -> x._2.as[Map[String, JsValue]].map{y =>
                y._1 -> y._2.as[List[String]]
            }
        }
    }

    def postGeneratePanelJob(uid: String, panelResult: JsValue): Unit = {
        alTempLog(s"generate panel result = $panelResult")

        val success = panelResult.asOpt[Map[String, JsValue]]
        success match {
            case Some(_) =>
                val rid = rd.hget(uid, "rid").map(x=>x).getOrElse(throw new Exception("not found rid"))
                jv2map(panelResult).foreach{ x=>
                    x._2.foreach{y=>
                        val panel = y._2.mkString(",")
                        rd.sadd(rid, panel)
                        rd.hset(panel, "ym", x._1)
                        rd.hset(panel, "mkt", y._1)
                    }
                }

                val msg = Map(
                    "type" -> "generate_panel_result",
                    "result" -> panelResult.toString
                )
                alWebSocket(uid).post(msg)
            case None => Unit
        }

        val agent = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
        agent ! refundNodeForRole("splitgeneratepanelslave")
    }

    def preSplitPanelJob(uid: String): Unit = {
        val rid = phRedisDriver().commonDriver.hget(uid, "rid").map(x=>x).getOrElse(throw new Exception("not found rid"))
        val panelLst = phRedisDriver().commonDriver.smembers(rid).map(x=>x.map(_.get)).getOrElse(throw new Exception("panel list is none"))

        panelLst.foreach{ panel =>
            pushSplitPanelJobs(alMaxRunning(uid, panel, rid))
        }
    }

    def postSplitPanelJob(item: alMaxRunning, parent: String, subs: List[String]): Unit ={
        alTempLog(s"split panel result, parent = $parent")
        alTempLog(s"split panel result, subs = $subs")

        if(parent.isEmpty || subs.isEmpty)
            alTempLog("split error, result is empty")
        else {
            val msg = Map(
                "type" -> "progress_calc",
                "txt" -> "分拆文件完成",
                "progress" -> "1"
            )
            alWebSocket(item.uid).post(msg)

            val panel = item.tid
            rd.hset(panel, "tid", subs.head)

            val arg = alMaxRunning(
                uid = item.uid,
                tid = parent,
                parent = panel,
                subs = subs.map{x =>
                    alMaxRunning(item.uid, x, parent, Nil)
                }
            )
            self ! pushGroupJob(arg)
        }

        val agent = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
        agent ! refundNodeForRole("splitsplitpanelslave")
    }

    def preGroupJob(item: alMaxRunning): Unit ={
        pushGroupJobs(item)
    }

    def postGroupJob(item: alMaxRunning): Unit ={
        alTempLog(s"group data result, parent = ${item.tid}")
        val subs = item.subs.map(_.tid).mkString(",")
        alTempLog(s"group data result, subs = $subs")

        val success = !subs.isEmpty
        if(success){
            val msg = Map(
                "type" -> "progress_calc",
                "txt" -> "数据分组完成",
                "progress" -> "2"
            )
            alWebSocket(item.uid).post(msg)

            self ! pushScpJob(item)
        }

        val agent = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
        agent ! refundNodeForRole("splitgroupslave")
    }

    def preScpJob(item: alMaxRunning): Unit ={
        pushScpJobs(item)
    }

    def postScpJob(item: alMaxRunning): Unit ={
        releaseScpEnergy
        self ! pushCalcJob(item)

        val msg = Map(
            "type" -> "progress_calc",
            "txt" -> "SCP完成",
            "progress" -> "3"
        )
        alWebSocket(item.uid).post(msg)
    }

    def preCalcJob(item: alMaxRunning): Unit ={
        pushCalcJobs(item)

        val msg = Map(
            "type" -> "progress_calc",
            "txt" -> "等待计算",
            "progress" -> "4"
        )
        alWebSocket(item.uid).post(msg)
    }

    def postCalcJob(result: Boolean, uid: String, panel: String, v: Double, u: Double): Unit = {
        val rd = phRedisDriver().commonDriver
        val tid = rd.hget(panel, "tid").get

        if (result) {
            var sum = rd.get("sum:" + tid).getOrElse("0").toInt
            sum += 1
            rd.set("sum:" + tid, sum)

            val old_value = rd.hget("calced:" + tid, "value").getOrElse("0").toDouble
            val old_unit = rd.hget("calced:" + tid, "unit").getOrElse("0").toDouble
            rd.hset("calced:" + tid, "value", old_value + v)
            rd.hset("calced:" + tid, "unit", old_unit + u)

            if (sum == core_number) {
                rd.set("sum:" + tid, 0)
                alTempLog(s"$panel calc data => Success")
                self ! pushRestoreJob(uid, panel)

                val agent = context.actorSelection("akka.tcp://calc@" + masterIP + ":2551/user/agent-reception")
                agent ! refundNodeForRole("splitcalcslave")
            }
        } else {
            rd.set("sum:" + tid, 0)
            val msg = Map(
                "type" -> "error",
                "error" -> "cannot calc data"
            )
            alWebSocket(uid).post(msg)
            alTempLog("calc data => Failed")

            val agent = context.actorSelection("akka.tcp://calc@" + masterIP + ":2551/user/agent-reception")
            agent ! refundNodeForRole("splitcalcslave")
        }
    }

    def preRestoreJob(uid: String, panel: String): Unit ={
        pushRestoreJobs(uid, panel)

        val msg = Map(
            "type" -> "progress_calc",
            "txt" -> "正在入库",
            "progress" -> "91"
        )
        alWebSocket(uid).post(msg)
    }

    def postRestoreJob(result: Boolean, uid: String): Unit ={
        alTempLog(s"restore bosn result = $result")
        if(result){
            val msg = Map(
                "type" -> "progress_calc_result",
                "txt" -> "入库完成",
                "progress" -> "100"
            )
            alWebSocket(uid).post(msg)
        }

        val agent = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
        agent ! refundNodeForRole("splitrestorebsonslave")
    }
}