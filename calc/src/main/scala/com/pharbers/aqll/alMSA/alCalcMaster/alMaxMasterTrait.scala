package com.pharbers.aqll.alMSA.alCalcMaster

import java.util.UUID
import play.api.libs.json.JsValue
import akka.actor.{Actor, ActorRef}
import scala.collection.immutable.Map
import com.pharbers.driver.redis.phRedisDriver
import com.pharbers.aqll.alCalaHelp.alLog.alTempLog
import com.pharbers.aqll.alStart.alHttpFunc.alPanelItem
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxMaster._
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait._
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.group._
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alMaxRunning
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.refundNodeForRole
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger.push_restore_job
import com.pharbers.aqll.alCalaHelp.alWebSocket.alWebSocket

trait alMaxMasterTrait extends alCalcYMTrait with alGeneratePanelTrait
                        with alSplitPanelTrait with alGroupDataTrait with alScpQueueTrait
                        with alCalcDataTrait with alRestoreBsonTrait{ this : Actor =>
    val rd =  phRedisDriver().commonDriver

    def preCalcYMJob(item: alPanelItem): Unit = {
        pushCalcYMJobs(item)
    }

    def postCalcYMJob(ym: List[String], mkt: List[String]): Unit = {
        alTempLog(s"calcYM result, ym = $ym, mkt = $mkt")

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

    def postGeneratePanelJob(uid: String, panelResult: JsValue): Unit = {
        alTempLog(s"generate panel result = $panelResult")
        val rid = rd.hget(uid, "rid").map(x=>x).getOrElse(throw new Exception("not found rid"))
        def jv2map(data: JsValue): Map[String, Map[String, List[String]]] ={
            data.as[Map[String, JsValue]].map{ x =>
                x._1 -> x._2.as[Map[String, JsValue]].map{y =>
                    y._1 -> y._2.as[List[String]]
                }
            }
        }

        jv2map(panelResult).foreach{ x=>
            x._2.foreach{y=>
                val panel = y._2.mkString(",")
                rd.sadd(rid, panel)
                rd.hset(panel, "ym", x._1)
                rd.hset(panel, "mkt", y._1)
            }
        }

        val agent = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
        agent ! refundNodeForRole("splitgeneratepanelslave")
    }

    def preSplitPanelJob(uid: String): Unit = {
        val rid = phRedisDriver().commonDriver.hget(uid, "rid").map(x=>x).getOrElse(throw new Exception("not found rid"))
        val panelLst = phRedisDriver().commonDriver.smembers(rid).map(x=>x.map(_.get)).getOrElse(throw new Exception("rid list is none"))

        panelLst.foreach{ panel =>
            pushSplitPanelJobs(alMaxRunning(uid, panel, rid))
        }
    }

    def postSplitPanelJob(item: alMaxRunning, parent: String, subs: List[String]): Unit ={
        if(parent.isEmpty || subs.isEmpty)
            alTempLog("split error, result is empty")
        else {
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
//        self ! pushScpJob(item)
//        val msg = Map(
//            "type" -> "progress_calc",
//            "txt" -> "等待计算",
//            "progress" -> "6"
//        )
//        alWebSocket(item.uid).post(msg)



        val agent = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
        agent ! refundNodeForRole("splitgroupslave")
    }

    def preScpJob(item: alMaxRunning) ={
        pushScpJobs(item)
        val msg = Map(
            "type" -> "progress_calc",
            "txt" -> "正在发送",
            "progress" -> "7"
        )
        alWebSocket(item.uid).post(msg)
    }

    def postScpJob(item: alMaxRunning) ={
        self ! pushCalcJob(item)
        val msg = Map(
            "type" -> "progress_calc",
            "txt" -> "等待计算",
            "progress" -> "8"
        )
        alWebSocket(item.uid).post(msg)
    }

    def preCalcJob(item: alMaxRunning) ={
        pushCalcJobs(item)
        val msg = Map(
            "type" -> "progress_calc",
            "txt" -> "正在计算",
            "progress" -> "10"
        )
        alWebSocket(item.uid).post(msg)
    }

    def postCalcJob(uid: String, tid: String, v: Double, u: Double, result: Boolean) {
        var msg = Map[String, String]()
        if (result) {
            val phRedisSet= phRedisDriver().phSetDriver
            val user_cr = s"calcResultUid${uid}"
            val cr = s"calcResultTid${tid}"
            val map = Map(user_cr -> cr, "value" -> v, "units" -> u)
            phRedisSet.sadd(s"${user_cr}", map, dealSameMapFunc)

            val redisDriver = rd
            var sum = redisDriver.get(s"Uid${uid}calcSum").get.toInt
            sum += 1
            redisDriver.set(s"Uid${uid}calcSum", sum)
            if(sum == core_number){
                sum = 0
                redisDriver.set(s"Uid${uid}calcSum", sum)
                msg = Map(
                    "type" -> "progress_calc",
                    "txt" -> "计算完成",
                    "progress" -> "11"
                )
                alWebSocket(uid).post(msg)
                val a = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
                a ! refundNodeForRole("splitcalcslave")
                self ! push_restore_job(uid)
            }

        } else {
            msg = Map(
                "type" -> "progress_calc",
                "txt" -> "计算失败",
                "progress" -> "12"
            )
            alWebSocket(uid).post(msg)
            val a = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
            a ! refundNodeForRole("splitcalcslave")
        }
    }

    def preRestoreJob(uid: String, sender: ActorRef) ={
        println("正在入库")
        pushRestoreJob(uid, sender)
        val msg = Map(
            "type" -> "progress_calc",
            "txt" -> "正在入库",
            "progress" -> "13"
        )
        alWebSocket(uid).post(msg)
    }

    def postRestoreJob(bool: Boolean, uid: String) ={
        val a = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
        a ! refundNodeForRole("splitrestorebsonslave")
        println(s"还原数据库结果 => ${bool}")
        var msg = Map[String, String]()
        if (bool) {
            msg = Map(
                "type" -> "progress_calc_result",
                "txt" -> "入库完成",
                "progress" -> "100"
            )
        } else {
            msg = Map(
                "type" -> "progress_calc_result",
                "txt" -> "入库失败",
                "progress" -> "100"
            )
        }
        alWebSocket(uid).post(msg)
    }
}