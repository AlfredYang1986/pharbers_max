package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.actor.SupervisorStrategy.Restart
import com.pharbers.aqll.alCalcHelp.alLog.alTempLog
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.panelMsg._
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.takeNodeForRole
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props, SupervisorStrategy}

/**
  * Created by jeorch on 17-10-11.
  *     Modify by clock on 2017.12.19
  */
object alGeneratePanelSlave {
    def props = Props[alGeneratePanelSlave]
    def name = "generate-panel-slave"
}

class alGeneratePanelSlave extends Actor with ActorLogging {
    override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
        case _ => Restart
    }

    override def receive: Receive = {
        case generate_panel_hand() => {
            //TODO ask shenyong
            implicit val t = Timeout(2 seconds)
            val a = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
            val f = a ? takeNodeForRole("splitgeneratepanelslave")
            if (Await.result(f, t.duration).asInstanceOf[Boolean])
                sender ! generate_panel_hand()
            else Unit
        }

        case generate_panel_start_impl(panelJob) => {
            val counter = context.actorOf(alCommonErrorCounter.props)
            val cur = context.actorOf(alGeneratePanelCameo.props(panelJob, counter))
            cur.tell(generate_panel_start_impl(panelJob), sender)
        }

        case msg: AnyRef => alTempLog(s"Warning! Message not delivered. alGeneratePanelSlave.received_msg=$msg")
    }

}
