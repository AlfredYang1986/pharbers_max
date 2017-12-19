package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.routing.BroadcastPool
import akka.pattern.ask
import akka.util.Timeout
import com.pharbers.aqll.alCalaHelp.alMaxDefines._
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcData.{calc_data_start, calc_data_sum, calc_unpkg}
import com.pharbers.aqll.alMSA.alMaxSlaves.alCalcDataSlave
import com.pharbers.driver.redis.phRedisDriver
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import com.pharbers.alCalcMemory.alprecess.alsplitstrategy.server_info
import com.pharbers.aqll.alCalaHelp.alWebSocket.alWebSocket
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.queryIdleNodeInstanceInSystemWithRole
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxMaster.calcSchedule

import scala.collection.immutable.Map
import scala.concurrent.Await
import scala.concurrent.stm._
import scala.concurrent.duration._
import scala.math.BigDecimal

/**
  * Created by alfredyang on 13/07/2017.
  */
trait alCalcDataTrait { this : Actor =>
    import scala.concurrent.ExecutionContext.Implicits.global

    val core_number: Int = server_info.cpu
    val calc_router = createCalcRouter
    val calc_jobs = Ref(List[alMaxRunning]())
    val calc_schdule = context.system.scheduler.schedule(2 second, 3 second, self, calcSchedule())

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

    def canCalcJob : Boolean = {
        implicit val t = Timeout(2 seconds)
        val a = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
        val f = a ? queryIdleNodeInstanceInSystemWithRole("splitcalcslave")
        Await.result(f, t.duration).asInstanceOf[Int] > 0        // TODO：现在只有一个，以后由配置文件修改
    }

    def schduleCalcJob = {
        if (canCalcJob) {
            atomic { implicit thx =>
                val tmp = calc_jobs.single.get
                if (tmp.isEmpty) Unit
                else {
                    calcData(tmp.head)
                    calc_jobs() = calc_jobs().tail
                }
            }
        }
    }

    def calcData(item: alMaxRunning) {
        val cur = context.actorOf(alCameoCalcData.props(item, self, calc_router))
        cur ! calc_unpkg(item.tid, self)
        val redisDriver = phRedisDriver().commonDriver
        redisDriver.set(s"Uid${item.uid}calcSum",0)
        val msg = Map(
            "type" -> "progress_calc",
            "progress" -> "10",
            "txt" -> "正在计算中"
        )
        alWebSocket(item.uid).post(msg)
    }

    def doSum(items: alMaxRunning, s: ActorRef) {
        val redisDriver = phRedisDriver().commonDriver
        var sum = redisDriver.get(s"Uid${items.uid}calcSum").get.toInt
        println(s"&& master doSum sum = ${sum}")
        sum += 1
        redisDriver.set(s"Uid${items.uid}calcSum", sum)
        if(sum == core_number){
            println("开始求和")
            sum = 0
            redisDriver.set(s"Uid${items.uid}calcSum", sum)
            s ! PoisonPill
            val cur = context.actorOf(alCameoCalcData.props(items, self, calc_router))
            cur ! calc_data_sum()
            val msg = Map(
                "type" -> "progress_calc",
                "progress" -> "11",
                "txt" -> "正在计算中"
            )
            alWebSocket(items.uid).post(msg)
        }
    }
    val dealSameMapFunc = (m1 : Map[String, Any], m2 : Map[String, Any]) => {
        m1.map(x => (x._1 -> (x._2.toString.toDouble + m2.get(x._1).get.toString.toDouble)))
    }
}

object alCameoCalcData {
    case class calc_unpkg(tid: String, s: ActorRef)
    case class calc_data_start()
    case class calc_data_hand()
    case class calc_data_hand2(item: alMaxRunning)
    case class calc_data_start_impl(item : alMaxRunning)
    case class calc_data_start_impl2(item : alMaxRunning)
    case class calc_data_start_impl3(sub_item : alMaxRunning, items : alMaxRunning)
    case class calc_data_sum()
    case class calc_data_average(item : alMaxRunning)
    case class calc_data_average_pre(avg_path: String)
    case class calc_data_average_one(avg_path: String, bsonpath: String)
    case class calc_data_average_post(item : alMaxRunning, avg_path: String, bsonpath: String)
    case class calc_data_result(uid: String, tid: String,v: Double, u: Double, result: Boolean)
    case class calc_data_timeout()

    def props(item: alMaxRunning,
              owner: ActorRef,
              router: ActorRef) = Props(new alCameoCalcData(item, owner, router))
}

class alCameoCalcData (item: alMaxRunning,
                       owner: ActorRef,
                       router: ActorRef) extends Actor with ActorLogging {

    import alCameoCalcData._

    val core_number = server_info.cpu
    var sed = 0
    var cur = 0
    var tol = 0
    //    val calcing_jobs = Ref(List[alMaxProperty]())     // only for calcing jobs

    override def receive: Receive = {
        case calc_data_timeout() => {
            log.info("timeout occur")
            shutCameo(calc_data_timeout())
        }

        case calc_unpkg(tid, s) => router ! calc_unpkg(tid, self)

        case _ : calc_data_start => router ! calc_data_hand2(item)

        case calc_data_hand2(it) => {
            log.info("&& T3 START &&")
            val t3 = startDate()
            println("&& T3 && alCameoCalcData.calc_data_hand")
            tol = it.subs.length
            if (sed < tol / core_number) {
                val tmp = for (index <- sed * core_number to (sed + 1) * core_number - 1) yield it.subs(index)
                sender ! calc_data_start_impl(alMaxRunning(item.uid, item.tid, item.parent, tmp.toList))
                sed += 1
                endDate("&& T3 && ", t3)
            }
            log.info("&& T3 END &&")
            shutCameo("End CalcHand Cameo")
        }

        case calc_data_sum() => {
            router ! calc_data_average(item)
            shutCameo("End CalcSum Cameo")
        }

        case msg : Any => log.info(s"Error msg=[${msg}] was not delivered.in actor=${self}")
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val calc_timer = context.system.scheduler.scheduleOnce(6000 minute) {
        self ! calc_data_timeout()
    }

    def shutCameo(msg : AnyRef) = {
        log.info(s"stopping calc data cameo msg=${msg}")
        calc_timer.cancel()
        context.stop(self)
    }

}