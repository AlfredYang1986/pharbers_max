package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.routing.BroadcastPool
import akka.pattern.ask
import akka.util.Timeout
import com.pharbers.aqll.alCalaHelp.alMaxDefines.{alCalcParmary, alMaxProperty}
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.queryIdleNodeInstanceInSystemWithRole
import com.pharbers.aqll.alMSA.alMaxSlaves.alRestoreBsonSlave

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.stm._

/**
  * Created by jeorch on 17-10-30.
  */
trait alRestoreBsonTrait { this : Actor =>
    // TODO : query instance from agent
    def createRestoreBsonRouter =
        context.actorOf(
            ClusterRouterPool(BroadcastPool(1),
                ClusterRouterPoolSettings(
                    totalInstances = 1,
                    maxInstancesPerNode = 1,
                    allowLocalRoutees = false,
                    useRole = Some("splitrestorebsonslave")
                )
            ).props(alRestoreBsonSlave.props), name = "restore-bson-router")

    val restore_router = createRestoreBsonRouter

    def pushRestoreJob(coll : String, sub_uuid : String, s : ActorRef) = {
        atomic { implicit thx =>
            restore_jobs() = restore_jobs() :+ (coll, sub_uuid, s)
        }
    }

    def canSchduleRestoreJob : Boolean = {
        implicit val t = Timeout(2 seconds)
        val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
        val f = a ? queryIdleNodeInstanceInSystemWithRole("splitrestorebsonslave")
        // val f = a ? queryIdleNodeInstanceInSystemWithRole("splitcalcslave")
        Await.result(f, t.duration).asInstanceOf[Int] > 0        // TODO：现在只有一个，以后由配置文件修改
    }

    def schduleRestoreJob = {
        if (canSchduleRestoreJob) {
            atomic { implicit thx =>
                val tmp = restore_jobs.single.get
                if (tmp.isEmpty) Unit
                else {
                    restoreBson(tmp.head._1, tmp.head._2, tmp.head._3)
                    restore_jobs() = restore_jobs().tail
                }
            }
        }
    }

    def restoreBson(coll : String, sub_uuid : String, s : ActorRef) = {
        val cur = context.actorOf(alCameoRestoreBson.props(coll, sub_uuid, s, self, restore_router))
        import alCameoRestoreBson._
        cur ! restore_bson_start()
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val restore_schdule = context.system.scheduler.schedule(1 second, 1 second, self, restore_bson_schedule())

    val restore_jobs = Ref(List[(String, String, ActorRef)]())
    case class restore_bson_schedule()

}

object alCameoRestoreBson {
    case class restore_bson_start()
    case class restore_bson_hand()
    case class restore_bson_start_impl(coll : String, sub_uuid : String)
    case class restore_bson_end(result : Boolean, sub_uuid : String)
    case class restore_bson_timeout()

    def props(coll : String,
              sub_uuid : String,
              originSender : ActorRef,
              owner : ActorRef,
              router : ActorRef) = Props(new alCameoRestoreBson(coll, sub_uuid, originSender, owner, router))
}

class alCameoRestoreBson(val coll : String,
                         val sub_uuid : String,
                         val originSender : ActorRef,
                         val owner : ActorRef,
                         val router : ActorRef) extends Actor with ActorLogging {

    import alCameoRestoreBson._

    var sign = false

    override def receive: Receive = {
        case restore_bson_timeout() => {
            log.debug("timeout occur")
            shutCameo(restore_bson_timeout())
        }
        case _ : restore_bson_start => router ! restore_bson_hand()
        case restore_bson_hand() => {
            if (sign == false) {
                sender ! restore_bson_start_impl(coll, sub_uuid)
                sign = true
            }
        }
        // TODO: 内存泄漏，稳定后修改
        case result : restore_bson_end => {
            //            slaveStatus send slave_status(true)
            //            owner forward result
            shutCameo(result)
        }
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val restore_timer = context.system.scheduler.scheduleOnce(10 minute) {
        self ! restore_bson_timeout()
    }

    def shutCameo(msg : AnyRef) = {
        originSender ! msg
        log.debug("stopping cameo restore bson")
        restore_timer.cancel()
        self ! PoisonPill
    }
}

