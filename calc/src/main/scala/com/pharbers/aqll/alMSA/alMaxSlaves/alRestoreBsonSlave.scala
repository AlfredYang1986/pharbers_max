package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props, SupervisorStrategy}
import akka.util.Timeout
import akka.pattern.ask
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.{refundNodeForRole, takeNodeForRole}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoRestoreBson.{restore_bson_end, restore_bson_hand, restore_bson_start_impl}
import scala.concurrent.duration._
import scala.concurrent.Await

/**
  * Created by jeorch on 17-10-30.
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
            implicit val t = Timeout(2 seconds)
            val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
            val f = a ? takeNodeForRole("splitrestorebsonslave")
            // val f = a ? takeNodeForRole("splitcalcslave")   // 在一台机器上实现和计算的互斥
            if (Await.result(f, t.duration).asInstanceOf[Boolean]) sender ! restore_bson_hand()
            else Unit
        }
        case restore_bson_start_impl(coll, sub_uuid) => {
            val counter = context.actorOf(alCommonErrorCounter.props)
            val cur = context.actorOf(alRestoreBsonComeo.props(coll, sub_uuid, sender, self, counter))
            cur.tell(restore_bson_start_impl(coll, sub_uuid), sender)
        }
        case cmd : restore_bson_end => {
            val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
            a ! refundNodeForRole("splitrestorebsonslave")
            // a ! refundNodeForRole("splitcalcslave") // 在一台机器上实现和计算的互斥
        }
    }

}
