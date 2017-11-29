package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props, SupervisorStrategy}
import akka.pattern.ask
import akka.util.Timeout
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.{refundNodeForRole, takeNodeForRole}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcData._

import scala.concurrent.duration._
import scala.concurrent.Await

/**
  * Created by alfredyang on 13/07/2017.
  */
object alCalcDataSlave {
    def props = Props[alCalcDataSlave]
    def name = "clac-data-slave"
}

class alCalcDataSlave extends Actor with ActorLogging {

    override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
        case _ => Restart
    }

    override def receive: Receive = {
        case calc_data_hand() => {
            implicit val t = Timeout(2 seconds)
            val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
            val f = a ? takeNodeForRole("splitcalcslave")
            if (Await.result(f, t.duration).asInstanceOf[Boolean]) sender ! calc_data_hand()
            else Unit
        }
        case calc_data_start_impl(item) => {
            val counter = context.actorOf(alCommonErrorCounter.props)
            val cur = context.actorOf(alCalcDataComeo.props(item, sender, self, counter))
            cur.tell(calc_data_start_impl(item), sender)
        }
        case cmd : calc_data_end => {
            val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
            a ! refundNodeForRole("splitcalcslave")
        }
        case calc_data_average3(item, avg_path, bsonpath) => {
            val counter = context.actorOf(alCommonErrorCounter.props)
            val cur = context.actorOf(alCalcDataComeo.props(item, sender, self, counter))
            cur.tell(calc_data_average_pre(item, avg_path, bsonpath), sender)
        }
        case msg : Any => log.info(s"Error msg=[${msg}] was not delivered.in actor=${self}")
    }
}