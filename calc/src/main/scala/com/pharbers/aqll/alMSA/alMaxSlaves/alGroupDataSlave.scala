package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.actor.SupervisorStrategy.Restart
import com.pharbers.aqll.alCalaHelp.alLog.alTempLog
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.group._
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.takeNodeForRole
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props, SupervisorStrategy}

/**
  * Created by alfredyang on 12/07/2017.
  *     Modify by clock on 2017.12.19
  */
object alGroupDataSlave {
    def props = Props[alGroupDataSlave]
    def name = "group-data-slave"
}

class alGroupDataSlave extends Actor with ActorLogging {
    override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
        case _ => Restart
    }

    override def receive: Receive = {
        case group_data_hand() => {
            //TODO ask shenyong
            implicit val t = Timeout(2 seconds)
            val a = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
            val f = a ? takeNodeForRole("splitgroupslave")
            if (Await.result(f, t.duration).asInstanceOf[Boolean])
                sender ! group_data_hand()
            else Unit
        }

        case group_data_start_impl(item) => {
            val counter = context.actorOf(alCommonErrorCounter.props)
            val cur = context.actorOf(alGroupDataComeo.props(item, sender, counter))
            cur.tell(group_data_start_impl(item), sender)
        }

        case msg: AnyRef => alTempLog(s"Warning! Message not delivered. alGroupDataSlave.received_msg=$msg")
    }
}
