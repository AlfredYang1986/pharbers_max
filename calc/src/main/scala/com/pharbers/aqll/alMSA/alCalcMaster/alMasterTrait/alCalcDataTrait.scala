package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.routing.BroadcastPool
import akka.util.Timeout
import com.pharbers.aqll.alCalaHelp.alMaxDefines.{alCalcParmary, alMaxProperty}
import com.pharbers.aqll.alCalcMemory.aljobs.alJob.split_group_jobs
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcData.{calc_data_start, calc_data_sum2}
import com.pharbers.aqll.alMSA.alMaxSlaves.alCalcDataSlave
import alCalcDataSlave.{slaveStatus, slave_status}
import com.pharbers.aqll.alCalc.almain.alShareData
import com.pharbers.aqll.alCalcMemory.alprecess.alsplitstrategy.server_info
import com.pharbers.aqll.alCalcOther.alfinaldataprocess.alRestoreColl
import com.pharbers.aqll.common.alFileHandler.alFilesOpt.alFileOpt

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

    def pushCalcJob(property : alMaxProperty, c : alCalcParmary, s : ActorRef) = {
        atomic { implicit thx =>
            calc_jobs() = calc_jobs() :+ (property, c, s)
        }
    }
    
    def setSlaveStatus = {
        slaveStatus send slave_status(true)
    }

    def canCalcGroupJob : Boolean = {
        slaveStatus().canDoJob
    }

    def schduleCalcJob = {
        if (canCalcGroupJob) {
            atomic { implicit thx =>
                val tmp = calc_jobs.single.get
                if (tmp.isEmpty) Unit
                else {
                    calcData(tmp.head._1, tmp.head._2, tmp.head._3)
                    calc_jobs() = calc_jobs().tail
                    slaveStatus send slave_status(false)
                }
            }
        }
    }

    def calcData(property : alMaxProperty, c : alCalcParmary, s : ActorRef) {
        val cur = context.actorOf(alCameoCalcData.props(c, property, s, self, calc_router))
        cur ! calc_data_start()
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val calc_schdule = context.system.scheduler.schedule(1 second, 1 second, self, calc_schedule())

    val calc_jobs = Ref(List[(alMaxProperty, alCalcParmary, ActorRef)]())
    case class calc_schedule()
}

object alCameoCalcData {
    case class calc_data_start()
    case class calc_data_hand()
    case class calc_data_start_impl(subs : alMaxProperty, c : alCalcParmary)
    case class calc_data_sum(sum : List[(String, (Double, Double, Double))])
    case class calc_data_sum2(path: String)
    case class calc_data_average(avg : List[(String, Double, Double)])
    case class push_insert_db_job(source : alFileOpt, avg : List[(String, Double, Double)], sub_uuid: String,  tmp: alMaxProperty)
    case class insertDbSchedule()
    case class do_insert_db(source : alFileOpt, avg : List[(String, Double, Double)], sub_uuid: String, tmp: alMaxProperty)
    case class after_insert_db()
    case class calc_data_result(v : Double, u : Double)
    case class calc_data_end(result : Boolean, property : alMaxProperty)
    case class calc_data_timeout()
    case class calc_slave_status()

    def props(c : alCalcParmary,
              property : alMaxProperty,
              originSender : ActorRef,
              owner : ActorRef,
              router : ActorRef) = Props(new alCameoCalcData(c, property, originSender, owner, router))
}

class alCameoCalcData ( val c : alCalcParmary,
                        val property : alMaxProperty,
                        val originSender : ActorRef,
                        val owner : ActorRef,
                        val router : ActorRef) extends Actor with ActorLogging {

    import alCameoCalcData._

    val core_number = server_info.cpu

    var sum : List[ActorRef] = Nil
    var sed = 0
    var cur = 0
    var tol = 0
//    val calcing_jobs = Ref(List[alMaxProperty]())     // only for calcing jobs

    override def receive: Receive = {
        case calc_data_timeout() => {
            log.debug("timeout occur")
            shutCameo(calc_data_timeout())
        }
        case _ : calc_data_start => {
            val spj = split_group_jobs(Map(split_group_jobs.max_uuid -> property.uuid))
            val (p, sb) = spj.result.map (x => x.asInstanceOf[(String, List[String])]).getOrElse(throw new Exception("split grouped error"))
            property.subs = sb map (x => alMaxProperty(p, x, Nil))
            tol = property.subs.length
            router ! calc_data_hand()
        }
        case calc_data_hand() => {
            if (sed < tol / core_number) {
                val tmp = for (index <- sed * core_number to (sed + 1) * core_number - 1) yield property.subs(index)

                sender ! calc_data_start_impl(alMaxProperty(property.parent, property.uuid, tmp.toList), c)
                sed += 1
            }
        }
        case calc_data_sum(sub_sum) => {
            // TODO : generate sum and average, post calc_data_average
            property.sum = property.sum ++: sub_sum

            sum = sender :: sum
            if (sum.length == tol / core_number) {
                property.isSumed = true
                property.sum = (property.sum.groupBy(_._1) map { x =>
                    (x._1, (x._2.map(z => z._2._1).sum, x._2.map(z => z._2._2).sum, x._2.map(z => z._2._3).sum))
                }).toList

                log.info(s"done for suming ${property.sum}")

                val mapAvg = property.sum.map { x =>
                    (x._1, (BigDecimal((x._2._1 / x._2._3).toString).toDouble),(BigDecimal((x._2._2 / x._2._3).toString).toDouble))
                }
                log.info(s"done for avg $mapAvg")
                sum.foreach(_ ! calc_data_average(mapAvg))
            }
        }
        case calc_data_sum2(path) => {
            // TODO: 开始读取segment分组文件
            property.sum = property.sum ++: readSegmentGroupData(path)
            
            sum = sender :: sum
            if (sum.length == tol / core_number) {
                property.isSumed = true
                property.sum = (property.sum.groupBy(_._1) map { x =>
                    (x._1, (x._2.map(z => z._2._1).sum, x._2.map(z => z._2._2).sum, x._2.map(z => z._2._3).sum))
                }).toList
        
                log.info(s"done for suming ${property.sum}")
        
                val mapAvg = property.sum.filterNot(x => x._2._1 == 0 && x._2._2 == 0).map { x =>
                    (x._1, (BigDecimal((x._2._1 / x._2._3).toString).toDouble),(BigDecimal((x._2._2 / x._2._3).toString).toDouble))
                }
                log.info(s"done for avg $mapAvg")
                sum.foreach(_ ! calc_data_average(mapAvg))
            }
        }

        case calc_data_result(v, u) => {
            property.finalValue += v
            property.finalUnit += u
        }
        case calc_data_end(result, mp) => {
            if (result) {
                cur += 1
                if (cur == tol / core_number) {
                    val r = calc_data_end(true, property)
//                    owner ! r
                    shutCameo(r)
                }
            } else {
                val r = calc_data_end(false, property)
//                owner ! r
                shutCameo(r)
            }
        }
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val calc_timer = context.system.scheduler.scheduleOnce(600 minute) {
        self ! calc_data_timeout()
    }

    def shutCameo(msg : AnyRef) = {
        originSender ! msg
//        slaveStatus send slave_status(true)
        log.debug("stopping group data cameo")
        calc_timer.cancel()
        context.stop(self)
    }
    
    def readSegmentGroupData(path: String) = {
        var segmentLst: List[(String, (Double, Double, Double))] = Nil
        val dir = alFileOpt(path)
        if (!dir.isExists)
            dir.createDir
        
        val source = alFileOpt(path + "/" + "segmentData")
        if (source.isExists) {
            source.enumDataWithFunc { line =>
                val s = alShareData.txtSegmentGroupData(line)
                segmentLst = segmentLst :+ (s.segement, (s.sales, s.units, s.calc))
            }
        }
        segmentLst
    }
}