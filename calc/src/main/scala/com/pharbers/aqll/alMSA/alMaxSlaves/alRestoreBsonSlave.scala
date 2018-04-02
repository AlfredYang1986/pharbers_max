package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.util.Timeout
import akka.pattern.ask
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.actor.SupervisorStrategy.Restart
import com.pharbers.aqll.alCalcHelp.alLog.alTempLog
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.restoreMsg._
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.takeNodeForRole
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props, SupervisorStrategy}

/**
  * Created by jeorch on 17-10-30.
  *     Modify by clock on 2017.12.21
  */
object alRestoreBsonSlave {
    def props = Props[alRestoreBsonSlave]
    def name = "restore-bson-slave"
}

class alRestoreBsonSlave extends Actor with ActorLogging {
    override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
        case _ => Restart
    }

    override def receive: Receive = {
        case restore_bson_hand() => {
            //TODO ask shenyong
            implicit val t = Timeout(5 seconds)
            val a = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
            val f = a ? takeNodeForRole("splitrestorebsonslave")
            if (Await.result(f, t.duration).asInstanceOf[Boolean])
                sender ! restore_bson_hand()
            else Unit
        }

        case restore_bson_start_impl(uid, panel) => {
            val counter = context.actorOf(alCommonErrorCounter.props)
            val cur = context.actorOf(alRestoreBsonComeo.props(uid, panel, counter))
            cur.tell(restore_bson_start_impl(uid, panel), sender)
        }

        case msg: AnyRef => alTempLog(s"Warning! Message not delivered. alSplitPanelSlave.received_msg=$msg")
    }
}
