package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props, SupervisorStrategy}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.{refundNodeForRole, takeNodeForRole}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoGeneratePanel.{generate_panel_end, generate_panel_hand, generate_panel_start_impl}

import scala.concurrent.Await

/**
  * Created by jeorch on 17-10-11.
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
            implicit val t = Timeout(2 seconds)
            val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
            val f = a ? takeNodeForRole("splitgeneratepanelslave")
//            val f = a ? takeNodeForRole("splitcalcslave")   // 在一台机器上实现和计算的互斥
            if (Await.result(f, t.duration).asInstanceOf[Boolean]) {
                sender ! generate_panel_hand()
            }
            else Unit
        }
        case generate_panel_start_impl(panel_job) => {
            val counter = context.actorOf(alCommonErrorCounter.props)
            val cur = context.actorOf(alGeneratePanelCameo.props(panel_job, sender, self, counter))
            cur.tell(generate_panel_start_impl(panel_job), sender)
        }
        case generate_panel_end(result, panelLst) => {
            val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
            a ! refundNodeForRole("splitgeneratepanelslave")
//            a ! refundNodeForRole("splitcalcslave") // 在一台机器上实现和计算的互斥
        }

        case msg : AnyRef => log.info(s"Warning! Message not delivered. alGeneratePanelSlave.received_msg=${msg}")

    }

}
