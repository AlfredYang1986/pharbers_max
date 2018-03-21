package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.actor.SupervisorStrategy.Restart
import com.pharbers.aqll.alCalcHelp.alLog.alTempLog
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.splitPanelMsg._
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.takeNodeForRole
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props, SupervisorStrategy}

/**
  * Created by alfredyang on 12/07/2017.
  *     Modify by clock on 2017.12.19
  */
object alSplitPanelSlave {
    def props = Props[alSplitPanelSlave]
    def name = "split-panel-slave"
}

class alSplitPanelSlave extends Actor with ActorLogging {
    override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
        case _ => Restart
    }

    override def receive: Receive = {
        case split_panel_hand() => {
            //TODO ask shenyong
            implicit val t = Timeout(2 seconds)
            val agent = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
            val f = agent ? takeNodeForRole("splitsplitpanelslave")
            if (Await.result(f, t.duration).asInstanceOf[Boolean])
                sender ! split_panel_hand()
            else Unit
        }

        case split_panel_start_impl(item) => {
            val counter = context.actorOf(alCommonErrorCounter.props)
            val cur = context.actorOf(alSplitPanelComeo.props(item, counter))
            cur.tell(split_panel_start_impl(item), sender)
        }

        case msg: AnyRef => alTempLog(s"Warning! Message not delivered. alSplitPanelSlave.received_msg=$msg")
    }
}
