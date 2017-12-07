package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props, SupervisorStrategy}
import akka.pattern.ask
import akka.util.Timeout
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alMaxRunning
import com.pharbers.aqll.alCalcMemory.aljobs.alJob.split_group_jobs
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.{refundNodeForRole, takeNodeForRole}
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxMaster.masterIP
import com.pharbers.aqll.common.alFileHandler.fileConfig.{group, memorySplitFile, sync}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcData._
import com.pharbers.aqll.alMSA.alMaxCmdMessage.alCmdActor
import com.pharbers.aqll.alMSA.alMaxCmdMessage.alCmdActor.{unpkgend, unpkgmsgMutiPath}

import scala.concurrent.duration._
import scala.concurrent.Await

/**
  * Created by alfredyang on 13/07/2017.
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
            val sync_pkg = s"${memorySplitFile}${sync}${tid}"
            val group_pkg = s"${memorySplitFile}${group}${tid}"
            cmdActor ! unpkgmsgMutiPath(sync_pkg :: group_pkg ::Nil, ".", s)
        }
        case unpkgend(s) => s ! calc_data_start()

        case calc_data_hand2(item_tmp) => {
            implicit val t = Timeout(2 seconds)
            val a = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
            val f = a ? takeNodeForRole("splitcalcslave")
            if (Await.result(f, t.duration).asInstanceOf[Boolean]) {
                val spj = split_group_jobs(Map(split_group_jobs.max_uuid -> item_tmp.tid))
                val (p, sb) = spj.result.map (x => x.asInstanceOf[(String, List[String])]).getOrElse(throw new Exception("split grouped error"))
                item_tmp.subs = sb map (x => alMaxRunning(item_tmp.uid, x, p, Nil))
                sender ! calc_data_hand2(item_tmp)
            } else Unit
        }
        case calc_data_start_impl(item) => {
            val counter = context.actorOf(alCommonErrorCounter.props)
            val cur = context.actorOf(alCalcDataComeo.props(item, sender, self, counter))
            cur.tell(calc_data_start_impl(item), sender)
        }
        case calc_data_average(items, avg_path) => {
            val counter = context.actorOf(alCommonErrorCounter.props)
            val cur = context.actorOf(alCalcDataComeo.props(items, sender, self, counter))
            cur.tell(calc_data_average_pre(avg_path), sender)
        }
        case msg : Any => log.info(s"Error msg=[${msg}] was not delivered.in actor=${self}")
    }
}