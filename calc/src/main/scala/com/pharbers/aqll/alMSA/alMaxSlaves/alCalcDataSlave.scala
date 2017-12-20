package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props, SupervisorStrategy}
import akka.pattern.ask
import akka.util.Timeout
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alMaxRunning
import com.pharbers.aqll.alCalcMemory.aljobs.alJob.split_group_jobs
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.takeNodeForRole
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.calcMsg._
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.scpMsg.{unpkgend, unpkgmsgMutiPath}
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import com.pharbers.aqll.common.alFileHandler.fileConfig.{calc, group, memorySplitFile, root, sync}
import com.pharbers.aqll.alMSA.alMaxCmdJob.alCmdActor
import com.pharbers.driver.redis.phRedisDriver
import com.pharbers.aqll.common.alFileHandler.alFilesOpt.alFileOpt

import scala.concurrent.duration._
import scala.concurrent.Await

/**
  * Created by alfredyang on 13/07/2017.
  *     Modify by clock on 2017.12.20
  */
object alCalcDataSlave {
    def props = Props[alCalcDataSlave]
    def name = "clac-data-slave"
}

class alCalcDataSlave extends Actor with ActorLogging {
    override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
        case _ => Restart
    }

    override def receive: Receive = {
        case calc_unpkg(tid, s) => {
            val cmdActor = context.actorOf(alCmdActor.props())
            val sync_pkg = s"$memorySplitFile$sync$tid"
            val group_pkg = s"$memorySplitFile$group$tid"
            cmdActor ! unpkgmsgMutiPath(sync_pkg :: group_pkg ::Nil, root, s)
        }

        case unpkgend(s) => s ! calc_data_start()

        case calc_data_hand2(item) => {
            //TODO ask shenyong
            implicit val t = Timeout(2 seconds)
            val a = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
            val f = a ? takeNodeForRole("splitcalcslave")
            if (Await.result(f, t.duration).asInstanceOf[Boolean]) {
                val spj = split_group_jobs(Map(split_group_jobs.max_uuid -> item.tid))
                val (parent, sb) = spj.result.map (x => x.asInstanceOf[(String, List[String])]).getOrElse(throw new Exception("split grouped error"))
                item.subs = sb.map{x =>
                    phRedisDriver().commonDriver.sadd(s"calced:$parent", x)//this parent = item.tid
                    alMaxRunning(item.uid, x, parent, Nil)
                }
                sender ! calc_data_hand2(item)
            }
        }

        case calc_data_start_impl(item) => {
            val counter = context.actorOf(alCommonErrorCounter.props)
            val cur = context.actorOf(alCalcDataComeo.props(item, sender, self, counter))
            cur.tell(calc_data_start_impl(item), sender)
        }

        case calc_data_average(items) => {
            println(s"calc_data_average3 items = ${items}")

            // TODO: 开始读取segment分组文件
            items.sum = items.sum ++: readRedisSegment("segment")
            items.isSumed = true
            items.sum = (items.sum.groupBy(_._1) map { x =>
                (x._1, (x._2.map(z => z._2._1).sum, x._2.map(z => z._2._2).sum, x._2.map(z => z._2._3).sum))
            }).toList
            val path = s"${memorySplitFile}${calc}${items.tid}"
            val dir = alFileOpt(path)
            if (!dir.isExists)
                dir.createDir
            val file = alFileOpt(path + "/avg")
            if (!file.isExists)
                file.createFile
            items.sum.filterNot(x => x._2._1 == 0 && x._2._2 == 0).map { x =>
                val avg_elem = (x._1, (BigDecimal((x._2._1 / x._2._3).toString).toDouble),(BigDecimal((x._2._2 / x._2._3).toString).toDouble))
                file.appendData2File(s"${avg_elem._1},${avg_elem._2},${avg_elem._3}"::Nil)
            }
            log.info(s"done for avg $path")
            items.sum = Nil
            val commonDriver= phRedisDriver().commonDriver
            commonDriver.del("segment")

            val counter = context.actorOf(alCommonErrorCounter.props)
            val cur = context.actorOf(alCalcDataComeo.props(items, sender, self, counter))
            cur.tell(calc_data_average_pre(path + "/avg"), sender)
        }
        case msg : Any => log.info(s"Error msg=[${msg}] was not delivered.in actor=${self}")
    }

    def readRedisSegment(setName: String) = {
        var segmentLst: List[(String, (Double, Double, Double))] = Nil
        val phSetDriver = phRedisDriver().phSetDriver
        val phHashDriver = phRedisDriver().phHashDriver
        phSetDriver.smembers(setName).foreach{x =>
            val h = phHashDriver.hgetall(x)
            segmentLst = segmentLst :+ (x, (h.get("sales").get.toDouble, h.get("unit").get.toDouble, h.get("calc").get.toDouble))
        }
        segmentLst
    }
}