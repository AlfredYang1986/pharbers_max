package com.pharbers.aqll.alMSA.alCalcMaster

import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.groupMsg.pushGroupJob
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.scpMsg.pushScpJob
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.calcMsg.pushCalcJob
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.restoreMsg.pushRestoreJob
import akka.actor.Actor
import com.redis.RedisClient
import java.util.{Date, UUID}

import play.api.libs.json.JsValue
import java.text.SimpleDateFormat

import scala.collection.immutable.Map
import com.pharbers.driver.redis.phRedisDriver
import com.pharbers.aqll.alCalcHelp.alLog.alTempLog
import com.pharbers.aqll.alStart.alHttpFunc.alPanelItem
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait._
import com.pharbers.aqll.alCalcHelp.alWebSocket.phWebSocket
import com.pharbers.aqll.alCalcHelp.alMaxDefines.alMaxRunning
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.refundNodeForRole
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.{startAggregationCalcData, startCalc}

trait alMaxMasterTrait extends alCalcYMTrait with alGeneratePanelTrait
                        with alSplitPanelTrait with alGroupDataTrait with alScpQueueTrait
                        with alCalcDataTrait with alRestoreBsonTrait with alAggregationDataTrait with alGenerateDeliveryFIleTrait{ this : Actor =>
    val rd: RedisClient =  phRedisDriver().commonDriver
    val df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

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
            phWebSocket(uid).post(msg)
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
                        rd.hset(panel, "mkt", x._1)
                        rd.hset(panel, "ym", y._1)
                    }
                }

                val msg = Map(
                    "type" -> "generate_panel_result",
                    "result" -> panelResult.toString
                )
                phWebSocket(uid).post(msg)

                if(uid.startsWith("uid")) {
                    val agentTest = context.actorSelection("akka.tcp://calc@" + masterIP + ":2551/user/agent-reception")
                    agentTest ! startCalc(uid)
                }
            case None => Unit
        }

        val agent = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
        agent ! refundNodeForRole("splitgeneratepanelslave")
    }

    def preSplitPanelJob(uid: String): Unit = {
        val rid = rd.hget(uid, "rid").map(x=>x).getOrElse(throw new Exception("not found rid"))
        val panelLst = rd.smembers(rid).map(x=>x.map(_.get)).getOrElse(throw new Exception("panel list is none"))
        rd.set("overSum:" + uid, 0)

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
            phWebSocket(item.uid).post(msg)

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
            phWebSocket(item.uid).post(msg)

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
        phWebSocket(item.uid).post(msg)
    }

    def preCalcJob(item: alMaxRunning): Unit ={
        pushCalcJobs(item)

        val msg = Map(
            "type" -> "progress_calc",
            "txt" -> "等待计算",
            "progress" -> "4"
        )
        phWebSocket(item.uid).post(msg)
    }

    def postCalcJob(result: Boolean, uid: String, panel: String, v: Double, u: Double): Unit = {
        val tid = rd.hget(panel, "tid").get

        if (result) {
            var calcSum = rd.get("calcSum:" + tid).getOrElse("0").toInt
            calcSum += 1
            rd.set("calcSum:" + tid, calcSum)

            val old_value = rd.hget("calced:" + tid, "value").getOrElse("0").toDouble
            val old_unit = rd.hget("calced:" + tid, "unit").getOrElse("0").toDouble
            rd.hset("calced:" + tid, "value", old_value + v)
            rd.hset("calced:" + tid, "unit", old_unit + u)

            if (calcSum == core_number) {
                alTempLog(s"$panel calc data => Success")
                self ! pushRestoreJob(uid, panel)
//
//                val agent = context.actorSelection("akka.tcp://calc@" + masterIP + ":2551/user/agent-reception")
//                agent ! refundNodeForRole("splitcalcslave")
            }
        } else {
            var calcSum = rd.get("calcSum:" + tid).getOrElse("0").toInt
            calcSum += 1
            rd.set("calcSum:" + tid, calcSum)
            val msg = Map(
                "type" -> "error",
                "error" -> "cannot calc data"
            )
            phWebSocket(uid).post(msg)
            alTempLog(s"$panel calc data => Failed")

            if (calcSum == core_number) {
                val agent = context.actorSelection("akka.tcp://calc@" + masterIP + ":2551/user/agent-reception")
                agent ! refundNodeForRole("splitcalcslave")
            }
        }
    }

    def preRestoreJob(uid: String, panel: String): Unit ={
        pushRestoreJobs(uid, panel)

        val msg = Map(
            "type" -> "progress_calc",
            "txt" -> "正在入库",
            "progress" -> "91"
        )
        phWebSocket(uid).post(msg)
    }

    def postRestoreJob(result: Boolean, uid: String): Unit ={
        alTempLog(s"restore bosn result = $result")
        if(result){
            val rid = rd.hget(uid, "rid").map(x=>x).getOrElse(throw new Exception("not found rid"))
            val panelLst = rd.smembers(rid).map(x=>x.map(_.get)).getOrElse(throw new Exception("panel list is none"))
            val panelSum = panelLst.size
            var overSum = rd.get("overSum:" + uid).getOrElse("0").toInt
            overSum += 1
            rd.set("overSum:" + uid, overSum)
            val msg = Map(
                "type" -> "progress_calc",
                "txt" -> "入库完成",
                "progress" -> "99"
            )
            phWebSocket(uid).post(msg)

            if(overSum == panelSum){
                val msg = Map(
                    "type" -> "progress_calc_result",
                    "txt" -> "计算完成",
                    "progress" -> "100"
                )
                phWebSocket(uid).post(msg)

                alTempLog(s"计算完成 in ${df.format(new Date())}")

                if(uid.startsWith("uid")){
                    val agentTest = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
                    agentTest ! startAggregationCalcData(uid, Nil)
                }
            }
        } else {
            alTempLog(s"计算失败 in ${df.format(new Date())}")
            var overSum = rd.get("overSum:" + uid).getOrElse("0").toInt
            rd.set("overSum:" + uid, overSum += 1)
            val msg = Map(
                "type" -> "error",
                "error" -> "cannot restore bson"
            )
            phWebSocket(uid).post(msg)
        }

        val agent = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
        agent ! refundNodeForRole("splitcalcslave")
        agent ! refundNodeForRole("splitrestorebsonslave")
    }
    
    def preAggregationJob(uid: String, showLst: List[String]): Unit = {
        val rid = rd.hget(uid, "rid").map(x => x).getOrElse(throw new Exception("not found uid"))
        val tidDetails = rd.smembers(rid).get.map(x =>(rd.hget(x.get, "ym").get, rd.hget(x.get, "mkt").get, rd.hget(x.get, "tid")))
        if (showLst.isEmpty) {
            tidDetails.foreach( x => pushAggregationJobs(uid, x._3.get))
        } else {
            showLst.map( x => tidDetails.find(f => f._1 == x.split("-")(1) && f._2 == x.split("-")(0))).filterNot(_.isEmpty).foreach( x => pushAggregationJobs(uid, x.get._3.get))
        }
        
        // TODO: 暂时不删除，测试通过在删除
//        val panelLst = rd.smembers(rid).map(x=>x.map(_.get)).getOrElse(throw new Exception("panel list is none"))
//        panelLst.map(panel => rd.hget(panel, "tid").getOrElse(throw new Exception("not found tid"))).toList foreach{x =>
//            pushAggregationJobs(uid, x)
//        }

        val msg = Map(
            "type" -> "progress_calc_result_done",
            "txt" -> "正在合并",
            "progress" -> "10"
        )
        phWebSocket(uid).post(msg)
    }
    
    def postAggregationJob(uid: String, table: String, result: Boolean): Unit = {
        if (result) {
            val msg = Map(
                "type" -> "progress_calc_result_done",
                "txt" -> s"$table,合并结束",
                "progress" -> "100"
            )

            alTempLog(s"合并完成 in ${df.format(new Date())}")
            phWebSocket(uid).post(msg)
        } else {
            alTempLog(s"合并失败 in ${df.format(new Date())}")
            val msg = Map(
                "type" -> "error",
                "error" -> "cannot aggregation data"
            )
            phWebSocket(uid).post(msg)
        }
        val agent = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
        agent ! refundNodeForRole("splitaggregationslave")
    }

    def preGenerateDeliveryJob(uid: String, showLst: List[String]): Unit = {
        alTempLog(s"开始生成交付文件 ${df.format(new Date())}")

        val rid = rd.hget(uid, "rid").map(x => x).getOrElse(throw new Exception("not found uid"))

        val tidDetails = rd.smembers(rid).get.map(x =>(rd.hget(x.get, "ym").get, rd.hget(x.get, "mkt").get, rd.hget(x.get, "tid").get))
        if (showLst.isEmpty) {
            pushDeliveryJobs(uid, tidDetails.map(x => x._3).toList)
        } else {
            val listJob = showLst.map( x => tidDetails.find(f => f._1 == x.split("-")(1) && f._2 == x.split("-")(0))).filterNot(_.isEmpty).map(x => x.get._3)
            pushDeliveryJobs(uid, listJob)
        }

        val msg = Map(
            "type" -> "progress_delivery",
            "txt" -> "正在生成交付文件",
            "progress" -> "1"
        )
        phWebSocket(uid).post(msg)
    }

    def postGenerateDeliveryJob(uid: String, fileName: String, result: Boolean): Unit = {
        if (result) {
            val msg = Map(
                "type" -> "progress_delivery_result",
                "fileName" -> s"$fileName",
                "progress" -> "100"
            )

            alTempLog(s"生成完成 in ${df.format(new Date())}")
            phWebSocket(uid).post(msg)
        } else {
            alTempLog(s"生成失败 in ${df.format(new Date())}")
            val msg = Map(
                "type" -> "error",
                "error" -> s"cannot generate deliveryFile ,ErrorLog is ${fileName}"
            )
            phWebSocket(uid).post(msg)
        }
        val agent = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
        agent ! refundNodeForRole("splitdeliveryslave")
    }
}