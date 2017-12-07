package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.actor.SupervisorStrategy.Restart
import com.pharbers.aqll.alMSA.alMaxCmdMessage.alCmdActor
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxMaster.masterIP
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props, SupervisorStrategy}
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.{refundNodeForRole, takeNodeForRole}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoGroupData.{group_data_end, group_data_hand, group_data_start_impl}

/**
  * Created by alfredyang on 12/07/2017.
  */
object alGroupDataSlave {
    def props = Props[alGroupDataSlave]
    def name = "group-data-slave"
}

class alGroupDataSlave extends Actor with ActorLogging {
    def cmdActor = context.actorOf(alCmdActor.props())

    override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
        case _ => Restart
    }

    override def receive: Receive = {
        case group_data_hand() => {
            implicit val t = Timeout(2 seconds)
            val a = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
            val f = a ? takeNodeForRole("splitgroupslave")
            if (Await.result(f, t.duration).asInstanceOf[Boolean])
                sender ! group_data_hand()
            else Unit
        }

        case group_data_start_impl(item) => {
            val counter = context.actorOf(alCommonErrorCounter.props)
            val cur = context.actorOf(alGroupDataComeo.props(item, sender, self, counter))
            cur.tell(group_data_start_impl(item), sender)
        }

        case _ : group_data_end => {
            val a = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
            a ! refundNodeForRole("splitgroupslave")
        }
    }
}
