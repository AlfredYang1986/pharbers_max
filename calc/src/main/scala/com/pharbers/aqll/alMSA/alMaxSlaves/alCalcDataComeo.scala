package com.pharbers.aqll.alMSA.alMaxSlaves

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props, SupervisorStrategy}
import akka.actor.SupervisorStrategy.Escalate
import akka.routing.BroadcastPool
import com.pharbers.aqll.alCalaHelp.alMaxDefines._
import com.pharbers.aqll.alCalcMemory.aljobs.alJob.worker_calc_core_split_jobs
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger._
import com.pharbers.alCalcMemory.alprecess.alsplitstrategy.server_info
import com.pharbers.aqll.alCalcOther.alMessgae.alWebSocket
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcData._
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoSplitPanel.split_panel_timeout
import com.pharbers.aqll.common.alFileHandler.alFilesOpt.alFileOpt
import akka.actor.SupervisorStrategy.{Escalate, Restart}
import com.pharbers.aqll.alCalc.almain.alSegmentGroup
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxMaster.sumCalcJob
import com.pharbers.aqll.common.alFileHandler.fileConfig.{calc, memorySplitFile}
import com.pharbers.driver.redis.phRedisDriver

import scala.collection.immutable.Map
import scala.concurrent.stm.{Ref, atomic}
import scala.concurrent.duration._

/**
  * Created by alfredyang on 12/07/2017.
  */

//trait alCalcAtomicTrait { this: Actor =>
//}

object alCalcDataComeo {
    def props(item: alMaxRunning, originSender : ActorRef, owner : ActorRef, counter : ActorRef) =
        Props(new alCalcDataComeo(item, originSender, owner, counter))
    val core_number: Int = server_info.cpu
}

class alCalcDataComeo (item : alMaxRunning,
                       originSender : ActorRef,
                       owner : ActorRef,
                       counter : ActorRef) extends Actor with ActorLogging {
    
    var cur = 0
    var sed = 0
    var sum = 0
    var segment : List[String] = Nil
    import alCalcDataComeo._
    var r : alMaxRunning = null
    
    override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
        case _ => Escalate
    }
    
    override def postRestart(reason: Throwable) : Unit = {
        // TODO : 计算次数，重新计算
        counter ! canIReStart(reason)
    }
    
    override def receive: Receive = {
        case calc_data_timeout() => {
            log.info("timeout occur")
            shutSlaveCameo(split_panel_timeout())
        }
        case calc_data_sum2() => {
            val t8 = startDate()
            println("&& T8 && alCalcDataComeo.calc_data_sum2")
            val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
            a ! sumCalcJob(item, self)
            endDate("&& T8 && ", t8)
            log.info("&& T8 END &&")
        }

        case calc_data_average_pre(avg_path, path) =>  {
            impl_router ! calc_data_average_pre(avg_path, path)
        }
        case calc_data_average_one(avg_path, path) =>  {
            sender ! calc_data_average_post(item.subs(sed), avg_path, path)
            sed += 1
        }
        case calc_data_average_post(items, avg_path, path) =>  {
            sum += 1
            if (sum == core_number) {
                val redisDriver = phRedisDriver().commonDriver
                val bsonpath = UUID.randomUUID().toString
                println(s"&& => calc_data_average2.bsonpath=${bsonpath}")
                redisDriver.lpush(s"bsonPathUid${item.uid}", bsonpath)
                impl_router ! calc_data_average_one(avg_path, bsonpath)
            }
        }

        case calc_data_result(v, u) => {
            val phRedisSet= phRedisDriver().phSetDriver
            val user_cr = s"calcResultUid${item.uid}"
            val cr = s"calcResultTid${item.tid}"
            val map = Map(user_cr -> cr, "value" -> v, "units" -> u)
            phRedisSet.sadd(s"${user_cr}", map, f)
        }

        case calc_data_end(result, p) => {
            log.info("&& T11 START &&")
            val t11 = startDate()
            println("&& T11 && alCalcDataComeo.calc_data_end")
            if (result) {
                cur += 1
                if (cur == core_number) {
                    val msg = calc_data_end(true, p)
                    owner ! msg
                    shutSlaveCameo(msg)
                }
            } else {
                val msg = calc_data_end(false, p)
                owner ! msg
                shutSlaveCameo(msg)
            }
            endDate("&& T11 && ", t11)
            log.info("&& T11 END &&")
        }
        case calc_data_start_impl(_) => {
            log.info("&& T5 START &&")
            val t5 = startDate()
            println("&& T5 && alCalcDataComeo.calc_data_start_impl")
            r = item
            impl_router ! calc_data_hand()
            endDate("&& T5 && ", t5)
            log.info("&& T5 END &&")
        }
        case calc_data_hand() => {
            log.info("&& T6 START &&")
            val t6 = startDate()
            println("&& T6 && alCalcDataComeo.calc_data_hand")
            sum += 1
            if (r != null) {
//                sender ! calc_data_start_impl(alMaxProperty(r.parent, r.uuid, r.subs(sed) :: Nil), c)
                sender ! calc_data_start_impl3(r.subs(sed), r)
                sed += 1
                endDate("&& T6 && ", t6)
            }
//            if (sum == core_number) shutSlaveCameo("End calc_data_hand!")
            log.info("&& T6 END &&")
        }
        
        case canDoRestart(reason: Throwable) => super.postRestart(reason); self ! calc_data_start_impl(item)
        
        case cannotRestart(reason: Throwable) => {
            val msg = Map(
                "type" -> "error",
                "error" -> s"error with actor=${self}, reason=${reason}"
            )
            alWebSocket(item.uid).post(msg)

            self ! calc_data_end(false, r)
        }
        case msg : Any => log.info(s"Error msg=[${msg}] was not delivered.in actor=${self}")
    }
    
    import scala.concurrent.ExecutionContext.Implicits.global
//    val insert_db_schedule = context.system.scheduler.schedule(5 second, 2 second, self, insertDbSchedule())
    val timeoutMessager = context.system.scheduler.scheduleOnce(6000 minute) {
        self ! calc_data_timeout()
    }

    def shutSlaveCameo(msg : AnyRef) = {
        val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
        a ! msg
        log.info("shutting calc data cameo")
        timeoutMessager.cancel()
        context.stop(self)
    }

    val f = (m1 : Map[String, Any], m2 : Map[String, Any]) => {
        m1.map(x => (x._1 -> (x._2.toString.toDouble + m2.get(x._1).get.toString.toDouble)))
    }
    
    val impl_router =
        context.actorOf(BroadcastPool(core_number).props(alCalcDataImpl.props), name = "concert-calc-router")
}