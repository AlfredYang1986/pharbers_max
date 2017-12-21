package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.stm._
import scala.concurrent.Await
import akka.routing.BroadcastPool
import scala.concurrent.duration._
import scala.collection.immutable.Map
import com.pharbers.driver.redis.phRedisDriver
import com.pharbers.aqll.alCalcHelp.alMaxDefines._
import com.pharbers.aqll.alCalcHelp.alLog.alTempLog
import scala.concurrent.ExecutionContext.Implicits.global
import com.pharbers.aqll.alMSA.alMaxSlaves.alCalcDataSlave
import com.pharbers.aqll.alCalcHelp.alWebSocket.alWebSocket
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.calcMsg._
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import com.pharbers.alCalcMemory.alprecess.alsplitstrategy.server_info
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, PoisonPill, Props}
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.queryIdleNodeInstanceInSystemWithRole

/**
  * Created by alfredyang on 13/07/2017.
  *     Modify by clock on 2017.12.20
  */
trait alCalcDataTrait { this : Actor =>
    val core_number: Int = server_info.cpu
    val calc_router: ActorRef = createCalcRouter
    val calc_jobs = Ref(List[alMaxRunning]())

    //TODO shijian chuan can
    val calc_schdule: Cancellable = context.system.scheduler.schedule(2 second, 3 second, self, calcSchedule())

    def createCalcRouter = context.actorOf(
        ClusterRouterPool(BroadcastPool(1),
            ClusterRouterPoolSettings(
                totalInstances = 1,
                maxInstancesPerNode = 1,
                allowLocalRoutees = false,
                useRole = Some("splitcalcslave")
            )
        ).props(alCalcDataSlave.props), name = "calc-data-router")

    def pushCalcJobs(item: alMaxRunning) = {
        atomic { implicit thx =>
            calc_jobs() = calc_jobs() :+ item
        }
    }

    //TODO ask shenyong
    def canCalcJob : Boolean = {
        implicit val t = Timeout(2 seconds)
        val a = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
        val f = a ? queryIdleNodeInstanceInSystemWithRole("splitcalcslave")
        Await.result(f, t.duration).asInstanceOf[Int] > 0
    }

    def calcScheduleJobs = {
        if (canCalcJob) {
            atomic { implicit thx =>
                val tmp = calc_jobs.single.get
                if (tmp.isEmpty) Unit
                else {
                    calc_jobs() = calc_jobs().tail
                    doCalcData(tmp.head)
                }
            }
        }
    }

    def doCalcData(item: alMaxRunning) {
        phRedisDriver().commonDriver.set("sum:"+item.tid, 0)//Sum counter
        val cur = context.actorOf(alCameoCalcData.props(item, calc_router))
        cur ! calc_unpkg(item.tid, self)
    }

    //TODO ti dao bie chu
    def doSum(item: alMaxRunning, s: ActorRef) {
        val rd = phRedisDriver().commonDriver
        var sum = rd.get("sum:"+item.tid).get.toInt
        alTempLog(s"C3. router segment => Success$sum")
        sum += 1
        rd.set("sum:"+item.tid, sum)

        if(sum == core_number){
            sum = 0
            rd.set("sum:"+item.tid, 0)
            s ! PoisonPill

            val cur = context.actorOf(alCameoCalcData.props(item, calc_router))
            cur ! calc_data_sum()
        }
    }
}

object alCameoCalcData {
    def props(item: alMaxRunning, slaveActor: ActorRef) = Props(new alCameoCalcData(item, slaveActor))
}

class alCameoCalcData (item: alMaxRunning,
                       slaveActor: ActorRef) extends Actor with ActorLogging {
    val core_number = server_info.cpu
    var sed = 0
    var cur = 0
    var tol = 0

    override def receive: Receive = {
        case calc_unpkg(tid, _) => slaveActor ! calc_unpkg(tid, self)

        case calc_data_start() =>{
            alTempLog("C1. calc unzip => Success")

            val msg = Map(
                "type" -> "progress_calc",
                "txt" -> "开始计算",
                "progress" -> "5"
            )
            alWebSocket(item.uid).post(msg)

            slaveActor ! calc_data_hand2(item)
        }

        case calc_data_hand2(splitResult) => {
            tol = splitResult.subs.length
            if (sed < tol / core_number) {
                val tmp = for(index <- sed * core_number until (sed + 1) * core_number) yield {
                    splitResult.subs(index)
                }
                sender ! calc_data_start_impl(alMaxRunning(item.uid, item.tid, item.parent, tmp.toList))
                alTempLog("C2. calc split => Success")
                sed += 1
            }
            shutCameo
        }

        case calc_data_sum() =>
            slaveActor ! calc_data_average(item)
            shutCameo

        case msg: Any =>
            alTempLog(s"Warning! Message not delivered. alCameoCalcData.received_msg=$msg")
            shutCameo
    }

    def shutCameo = {
        alTempLog("stopping calc data cameo")
        self ! PoisonPill
    }
}