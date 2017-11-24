package com.pharbers.aqll.alMSA.alCalcMaster

import java.util.UUID
import akka.actor.{Actor, ActorRef}
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alMaxRunning
import com.pharbers.aqll.alCalcOther.alMessgae.alWebSocket
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait._
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxMaster.pushGroupJob
import com.pharbers.aqll.alStart.alHttpFunc.alPanelItem
import com.pharbers.driver.redis.phRedisDriver
import play.api.libs.json.JsValue
import scala.collection.immutable.Map

trait alMaxMasterTrait extends alCalcYMTrait with alGeneratePanelTrait
                        with alSplitPanelTrait with alGroupDataTrait
                        with alCalcDataTrait with alRestoreBsonTrait
                        with alScpQueueTrait { this : Actor =>

    def preCalcYMJob(item: alPanelItem, sender: ActorRef) = pushCalcYMJobs(item, sender)

    def preGeneratePanelJob(item: alPanelItem, sender: ActorRef) = {
        val rid = UUID.randomUUID().toString
        println("开始生成panel，本次计算流程的rid为 = " + rid)
        phRedisDriver().commonDriver.hset(item.uid, "rid", rid)
        pushGeneratePanelJobs(item, sender)
    }

    def postGeneratePanelJob(uid: String, panelResult: JsValue) = {
        def jv2map(data: JsValue): Map[String, Map[String, List[String]]] ={
            data.as[Map[String, JsValue]].map{ x =>
                x._1 -> x._2.as[Map[String, JsValue]].map{y =>
                    y._1 -> y._2.as[List[String]]
                }
            }
        }
        val rid = phRedisDriver().commonDriver.hget(uid, "rid")
                .map(x=>x).getOrElse(throw new Exception("not found uid"))
        jv2map(panelResult).foreach{x=>
            x._2.foreach{y=>
                val panel = y._2.mkString(",")
                phRedisDriver().commonDriver.sadd(rid, panel)
                phRedisDriver().commonDriver.hset(panel, "ym", x._1)
                phRedisDriver().commonDriver.hset(panel, "mkt", y._1)
//                phRedisDriver().commonDriver.hset(cid, "calcStep", 0)
            }
        }
    }

    def preSplitPanelJob(uid: String, sender: ActorRef) ={
        val rid = phRedisDriver().commonDriver.hget(uid, "rid")
                .map(x=>x).getOrElse(throw new Exception("not found uid"))
        val panelLst = phRedisDriver().commonDriver.smembers(rid)
                .map(x=>x.map(_.get)).getOrElse(throw new Exception("rid list is none"))
        panelLst.foreach{panel=>
            pushSplitPanelJob(alMaxRunning(uid, panel, ""), sender)
        }
        val msg = Map(
            "type" -> "progress_calc",
            "txt" -> "分拆文件中",
            "progress" -> "1"
        )
        alWebSocket(uid).post(msg)
    }

    def postSplitPanelJob(item: alMaxRunning, parent: String, subs: List[String]) ={
        if(parent.isEmpty || subs.isEmpty) println("拆分错了吧，空的")

        phRedisDriver().commonDriver.hset(item.tid, "tid", parent)

        item.tid = parent
        item.subs = subs.map{x=>
            phRedisDriver().commonDriver.sadd(parent, x)
            alMaxRunning(item.uid, x, parent)
        }
        self ! pushGroupJob(item)

        val msg = Map(
            "type" -> "progress_calc",
            "txt" -> "分拆文件完成",
            "progress" -> "3"
        )
        alWebSocket(item.uid).post(msg)
    }

    def preGroupJob(item: alMaxRunning, sender: ActorRef) ={
        pushGroupJobs(item, sender)
        val msg = Map(
            "type" -> "progress_calc",
            "txt" -> "文件分组中",
            "progress" -> "4"
        )
        alWebSocket(item.uid).post(msg)
    }

    def postGroupJob(item: alMaxRunning) ={
        println("123412341234 = " + item)
        val msg = Map(
            "type" -> "progress_calc",
            "txt" -> "等待计算",
            "progress" -> "6"
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