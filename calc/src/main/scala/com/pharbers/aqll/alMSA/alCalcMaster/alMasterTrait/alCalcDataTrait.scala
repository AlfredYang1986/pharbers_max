package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.routing.BroadcastPool
import akka.pattern.ask
import akka.util.Timeout
import com.pharbers.aqll.alCalaHelp.alMaxDefines._
import com.pharbers.aqll.alCalcMemory.aljobs.alJob.split_group_jobs
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcData.{calc_data_start, calc_data_sum2}
import com.pharbers.aqll.alMSA.alMaxSlaves.alCalcDataSlave
import com.pharbers.driver.redis.phRedisDriver
import com.pharbers.aqll.alCalc.almain.alShareData
import com.pharbers.aqll.common.alFileHandler.fileConfig.{calc, memorySplitFile}
import com.pharbers.alCalcMemory.alprecess.alsplitstrategy.server_info
import com.pharbers.aqll.alCalcOther.alMessgae.alWebSocket
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.queryIdleNodeInstanceInSystemWithRole
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxMaster.calcSchedule
import com.pharbers.aqll.common.alFileHandler.alFilesOpt.alFileOpt

import scala.collection.immutable.Map
import scala.concurrent.Await
import scala.concurrent.stm._
import scala.concurrent.duration._
import scala.math.BigDecimal

/**
  * Created by alfredyang on 13/07/2017.
  */
trait alCalcDataTrait { this : Actor =>
    def createCalcRouter =
        context.actorOf(
            ClusterRouterPool(BroadcastPool(1),
                ClusterRouterPoolSettings(
                    totalInstances = 1,
                    maxInstancesPerNode = 1,
                    allowLocalRoutees = false,
                    useRole = Some("splitcalcslave")
                )
            ).props(alCalcDataSlave.props), name = "calc-data-router")

    val calc_router = createCalcRouter
    val core_number: Int = server_info.cpu
    var sum = 0
    var uid = ""
    var tid = ""

    def pushCalcJobs(item: alMaxRunning, sender: ActorRef) = {
        atomic { implicit thx =>
            calc_jobs() = calc_jobs() :+ (item, sender)
        }
    }

    def canCalcGroupJob : Boolean = {
        implicit val t = Timeout(2 seconds)
        val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
        val f = a ? queryIdleNodeInstanceInSystemWithRole("splitcalcslave")
        Await.result(f, t.duration).asInstanceOf[Int] > 0        // TODO：现在只有一个，以后由配置文件修改
    }

    def schduleCalcJob = {
        if (canCalcGroupJob) {
            atomic { implicit thx =>
                val tmp = calc_jobs.single.get
                if (tmp.isEmpty) Unit
                else {
                    calcData(tmp.head._1, tmp.head._2)
                    calc_jobs() = calc_jobs().tail
                }
            }
        }
    }

    def calcData(item: alMaxRunning, sender: ActorRef) {
        val cur = context.actorOf(alCameoCalcData.props(item, sender, self, calc_router))
        cur ! calc_data_start()

        val msg = Map(
            "type" -> "progress_calc",
            "progress" -> "10",
            "txt" -> "正在计算中"
        )
        alWebSocket(item.uid).post(msg)
    }

    def doSum(item: alMaxRunning, s: ActorRef) {
        println("开始求和")
        // TODO: 现在单机多线程情况,需要时再写多机器多线
        // TODO: 因为是在Master结点上,所以改一下判断依据即可
        sum += 1
        if(sum == core_number){
            uid = item.uid
            tid = item.tid
            val cur = context.actorOf(alCameoCalcData.props(item, s, self, calc_router))
            cur ! calc_data_sum2()
            val msg = Map(
                "type" -> "progress_calc",
                "progress" -> "11",
                "txt" -> "正在计算中"
            )
            alWebSocket(item.uid).post(msg)
            sum = 0
        }
    }

    def finalResult(v : Double, u : Double) {
        val phRedisSet= phRedisDriver().phSetDriver
        val user_cr = s"calcResultUid${uid}"
        val cr = s"calcResultTid${tid}"
        val map = Map(user_cr -> cr, "value" -> v, "units" -> u)
        phRedisSet.sadd(s"${user_cr}", map, f)
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val calc_schdule = context.system.scheduler.schedule(2 second, 3 second, self, calcSchedule())

    val calc_jobs = Ref(List[(alMaxRunning, ActorRef)]())

    val f = (m1 : Map[String, Any], m2 : Map[String, Any]) => {
        m1.map(x => (x._1 -> (x._2.toString.toDouble + m2.get(x._1).get.toString.toDouble)))
    }
}

object alCameoCalcData {
    case class calc_data_start()
    case class calc_data_hand()
    case class calc_data_start_impl(item : alMaxRunning)
    case class calc_data_start_impl2(item : alMaxRunning)
    case class calc_data_sum2()
    case class calc_data_average(avg : List[(String, Double, Double)])
    case class calc_data_average2(avg_path: String, bsonpath: String)
    case class calc_data_result(v : Double, u : Double)
    case class calc_data_end(result : Boolean, item : alMaxRunning)
    case class calc_data_timeout()

    def props(item: alMaxRunning,
              originSender: ActorRef,
              owner: ActorRef,
              router: ActorRef) = Props(new alCameoCalcData(item, originSender, owner, router))
}

class alCameoCalcData ( val item : alMaxRunning,
                        val originSender : ActorRef,
                        val owner : ActorRef,
                        val router : ActorRef) extends Actor with ActorLogging {

    import alCameoCalcData._

    val core_number = server_info.cpu
    val tid = item.tid

    var sed = 0
    var cur = 0
    var tol = 0
    //    val calcing_jobs = Ref(List[alMaxProperty]())     // only for calcing jobs

    override def receive: Receive = {
        case calc_data_timeout() => {
            log.info("timeout occur")
            shutCameo(calc_data_timeout())
        }
        case _ : calc_data_start => {
            log.info("&& T1 && alCameoCalcData.calc_data_start")
            val t1 = startDate()
            println("&& T1 && alCameoCalcData.calc_data_start")

            val spj = split_group_jobs(Map(split_group_jobs.max_uuid -> item.tid))
            val (p, sb) = spj.result.map (x => x.asInstanceOf[(String, List[String])]).getOrElse(throw new Exception("split grouped error"))
            item.subs = sb map (x => alMaxRunning(item.uid, x, p, Nil))
            println(s"## p=${p}")
            println(s"## sb=${sb}")
            tol = item.subs.length
            router ! calc_data_hand()
            endDate("&& T1 &&", t1)
            log.info("&& T1 END &&")
        }
        case calc_data_hand() => {
            log.info("&& T3 START &&")
            val t3 = startDate()
            println("&& T3 && alCameoCalcData.calc_data_hand")
            if (sed < tol / core_number) {
                val tmp = for (index <- sed * core_number to (sed + 1) * core_number - 1) yield item.subs(index)

                sender ! calc_data_start_impl(alMaxRunning(item.uid, item.tid, item.parent, tmp.toList))
                sed += 1
                endDate("&& T3 && ", t3)
            }
            log.info("&& T3 END &&")
//            shutCameo("End Hand Cameo")
        }
        case calc_data_sum2() => {
            log.info("&& T9 START &&")
            val t9 = startDate()
            println("&& T9 && alCameoCalcData.calc_data_sum2")
            // TODO: 开始读取segment分组文件
            item.sum = item.sum ++: readRedisSegment("segment")
            item.isSumed = true
            item.sum = (item.sum.groupBy(_._1) map { x =>
                (x._1, (x._2.map(z => z._2._1).sum, x._2.map(z => z._2._2).sum, x._2.map(z => z._2._3).sum))
            }).toList
            val path = s"${memorySplitFile}${calc}${tid}"
            val dir = alFileOpt(path)
            if (!dir.isExists)
                dir.createDir
            val file = alFileOpt(path + "/" + "avg")
            if (!file.isExists)
                file.createFile

            val mapAvg = item.sum.filterNot(x => x._2._1 == 0 && x._2._2 == 0).map { x =>
                val avg_elem = (x._1, (BigDecimal((x._2._1 / x._2._3).toString).toDouble),(BigDecimal((x._2._2 / x._2._3).toString).toDouble))
                file.appendData2File(s"${avg_elem._1},${avg_elem._2},${avg_elem._3}"::Nil)
            }
            log.info(s"done for avg $path")

            originSender ! calc_data_average2(path + "/" + "avg", path)

            shutCameo("End Sum Cameo")
            endDate("&& T9 && ", t9)
            log.info("&& T9 END &&")
        }

        case msg : Any => log.info(s"Error msg=[${msg}] was not delivered.in actor=${self}")
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val calc_timer = context.system.scheduler.scheduleOnce(6000 minute) {
        self ! calc_data_timeout()
    }

    def shutCameo(msg : AnyRef) = {
        val commonDriver= phRedisDriver().commonDriver
        commonDriver.del("segment")
        log.info("stopping group data cameo")
        calc_timer.cancel()
        context.stop(self)
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