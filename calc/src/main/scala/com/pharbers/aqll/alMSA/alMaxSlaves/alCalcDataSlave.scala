package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props, SupervisorStrategy}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.{refundNodeForRole, takeNodeForRole}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcData.{calc_data_end, calc_data_hand, calc_data_start_impl}

import scala.concurrent.Await

/**
  * Created by alfredyang on 13/07/2017.
  */
object alCalcDataSlave {
    def props = Props[alCalcDataSlave]
    def name = "clac-data-slave"

//    import scala.concurrent.ExecutionContext.Implicits.global
//    case class state_agent(val isRunning : Boolean)
//    val stateAgent = Agent(state_agent(false))
//
//    case class slave_status(val canDoJob : Boolean)
//    val slaveStatus = Agent(slave_status(true))
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
        case calc_data_start_impl(lsp, c) => {
            val counter = context.actorOf(alCommonErrorCounter.props)
            val cur = context.actorOf(alCalcDataComeo.props(c, lsp, sender, self, counter))
            cur.tell(calc_data_start_impl(lsp, c), sender)
        }
        case cmd : calc_data_end => {
            val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
            a ! refundNodeForRole("splitcalcslave")
        }
        case msg : Any => log.info(s"Error msg=[${msg}] was not delivered.in actor=${self}")
    }
}