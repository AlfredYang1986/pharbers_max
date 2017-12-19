package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.actor.SupervisorStrategy.Restart
import com.pharbers.aqll.alCalaHelp.alLog.alTempLog
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.ymMsg._
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.takeNodeForRole
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props, SupervisorStrategy}

/**
  * Created by jeorch on 17-10-11.
  *     Modify by clock on 2017.12.19
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
            //TODO ask shenyong
            implicit val t = Timeout(2 seconds)
            val agent = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
            val f = agent ? takeNodeForRole("splitcalcymslave")
            if (Await.result(f, t.duration).asInstanceOf[Boolean])
                sender ! calcYM_hand()
            else Unit
        }

        case calcYM_start_impl(calcYMJob) => {
            val counter = context.actorOf(alCommonErrorCounter.props)
            val cur = context.actorOf(alCalcYMCameo.props(calcYMJob, counter))
            cur.tell(calcYM_start_impl(calcYMJob), sender)
        }

        case msg: AnyRef => alTempLog(s"Warning! Message not delivered. alCalcYMSlave.received_msg=$msg")
    }
}
