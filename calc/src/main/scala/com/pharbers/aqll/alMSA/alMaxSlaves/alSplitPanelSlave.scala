package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props, SupervisorStrategy}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.{refundNodeForRole, takeNodeForRole}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoSplitPanel.{split_panel_end, split_panel_hand, split_panel_start_impl}
import scala.concurrent.Await

/**
  * Created by alfredyang on 12/07/2017.
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
            implicit val t = Timeout(2 seconds)
            val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
            val f = a ? takeNodeForRole("splitsplitpanelslave")
            if (Await.result(f, t.duration).asInstanceOf[Boolean]) sender ! split_panel_hand()
            else Unit
        }
        case split_panel_start_impl(item) => {
            val counter = context.actorOf(alCommonErrorCounter.props)
            val cur = context.actorOf(alSplitPanelComeo.props(item, sender, self, counter))
            cur.tell(split_panel_start_impl(item), sender)
        }
        case cmd : split_panel_end => {
            val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
            a ! refundNodeForRole("splitsplitpanelslave")
        }
    }
}
