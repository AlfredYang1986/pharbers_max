package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, PoisonPill, Props}
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.routing.BroadcastPool
import akka.pattern.ask
import akka.util.Timeout
import com.pharbers.aqll.alCalaHelp.alMaxDefines._
import com.pharbers.aqll.alMSA.alMaxSlaves.alCalcDataSlave
import com.pharbers.driver.redis.phRedisDriver
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP

import scala.concurrent.ExecutionContext.Implicits.global
import com.pharbers.alCalcMemory.alprecess.alsplitstrategy.server_info
import com.pharbers.aqll.alCalaHelp.alWebSocket.alWebSocket
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.queryIdleNodeInstanceInSystemWithRole
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.calcMsg._
import com.pharbers.aqll.alCalaHelp.alLog.alTempLog

import scala.collection.immutable.Map
import scala.concurrent.Await
import scala.concurrent.stm._
import scala.concurrent.duration._

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
        phRedisDriver().commonDriver.hset(item.parent, "calcSum", 0)//Sum counter
        val cur = context.actorOf(alCameoCalcData.props(item, calc_router))
        cur ! calc_unpkg(item.tid, self)
    }

    def doSum(items: alMaxRunning, s: ActorRef) {
        val rd = phRedisDriver().commonDriver
        println("+++doSum++++items++++++++++++++++++"+items)

        var sum = rd.hget(items.parent, "calcSum").get.toInt//redisDriver.get(s"Uid${items.uid}calcSum").get.toInt
        println(s"&& master doSum sum = $sum")
        sum += 1
        rd.hset(items.parent, "calcSum", sum)//rd.set(s"Uid${items.uid}calcSum", sum)
        if(sum == core_number){
            println("开始求和")
            sum = 0
            rd.set(s"Uid${items.uid}calcSum", sum)
            s ! PoisonPill
//            val cur = context.actorOf(alCameoCalcData.props(items, self, calc_router))
//            cur ! calc_data_sum()
            val msg = Map(
                "type" -> "progress_calc",
                "progress" -> "11",
                "txt" -> "正在计算中"
            )
            alWebSocket(items.uid).post(msg)
        }
    }
    val dealSameMapFunc = (m1 : Map[String, Any], m2 : Map[String, Any]) => {
        m1.map(x => x._1 -> (x._2.toString.toDouble + m2(x._1).toString.toDouble))
    }
}

object alCameoCalcData {
    def props(item: alMaxRunning, router: ActorRef) = Props(new alCameoCalcData(item, router))
}

class alCameoCalcData (item: alMaxRunning,
                       router: ActorRef) extends Actor with ActorLogging {
    val core_number = server_info.cpu
    var sed = 0
    var cur = 0
    var tol = 0

    override def receive: Receive = {
        case calc_unpkg(tid, _) => router ! calc_unpkg(tid, self)

        case calc_data_start() =>
            alTempLog("C1. calc unzip => Success")
            router ! calc_data_hand2(item)

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
            shutCameo("End CalcHand Cameo")
        }

        case calc_data_timeout() => {
            log.info("timeout occur")
            shutCameo(calc_data_timeout())
        }

        case calc_data_sum() => {
            router ! calc_data_average(item)
            shutCameo("End CalcSum Cameo")
        }

        case msg : Any => log.info(s"Error msg=[${msg}] was not delivered.in actor=${self}")
    }

    val timeoutMessager = context.system.scheduler.scheduleOnce(6000 minute) {
        self ! calc_data_timeout()
    }

    def shutCameo(msg : AnyRef) = {
        log.info(s"stopping calc data cameo msg=${msg}")
        timeoutMessager.cancel()
        self ! PoisonPill
    }

}