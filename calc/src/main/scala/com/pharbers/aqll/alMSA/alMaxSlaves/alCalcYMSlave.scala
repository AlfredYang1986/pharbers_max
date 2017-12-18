package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.actor.SupervisorStrategy.Restart
import com.pharbers.aqll.alCalaHelp.alLog.alTempLog
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.ymMsg._
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props, SupervisorStrategy}
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.{refundNodeForRole, takeNodeForRole}

/**
  * Created by jeorch on 17-10-11.
  */
object alCalcYMSlave {
    def props = Props[alCalcYMSlave]
    def name = "calc-ym-slave"
}
class alCalcYMSlave extends Actor with ActorLogging {
    override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
        case _ => Restart
    }

    override def receive: Receive = {
        case calcYM_hand() => {
            val a = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
            a ! takeNodeForRole("splitcalcymslave")
            sender ! calcYM_hand()
        }

        case calcYM_start_impl(calcYM_job) => {
            val counter = context.actorOf(alCommonErrorCounter.props)
            val cur = context.actorOf(alCalcYMCameo.props(calcYM_job, self, counter))
            cur.tell(calcYM_start_impl(calcYM_job), sender)
        }

        case calcYM_end2(_, _, _) => {
            val a = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
            a ! refundNodeForRole("splitcalcymslave")
        }

        case msg : AnyRef => alTempLog(s"Warning! Message not delivered. alCalcYMSlave.received_msg=$msg")
    }

}
