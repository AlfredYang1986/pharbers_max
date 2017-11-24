package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props, SupervisorStrategy}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.{refundNodeForRole, takeNodeForRole}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoGroupData.{group_data_end, group_data_hand, group_data_start_impl}
import com.pharbers.aqll.alMSA.alMaxCmdMessage.{alCmdActor, unpkgend}
import scala.concurrent.Await

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
            val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
            val f = a ? takeNodeForRole("splitgroupslave")
            if (Await.result(f, t.duration).asInstanceOf[Boolean])
                sender ! group_data_hand()
            else Unit
        }

        case unpkgend(s) => {
            // TODO: 销毁解压消息
            context stop s
            sender ! group_data_hand()
        }

        case group_data_start_impl(item) => {
            val counter = context.actorOf(alCommonErrorCounter.props)
            val cur = context.actorOf(alGroupDataComeo.props(item, sender, self, counter))
            cur.tell(group_data_start_impl(item), sender)
        }

        case _ : group_data_end => {
            val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
            a ! refundNodeForRole("splitgroupslave")
        }
    }
}
