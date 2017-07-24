package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.actor.{Actor, ActorLogging, Props}
import akka.agent.Agent
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.{refundNodeForRole, takeNodeForRole}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcData.{calc_data_end, calc_data_hand, calc_data_start_impl}

/**
  * Created by alfredyang on 13/07/2017.
  */
object alCalcDataSlave {
    def props = Props[alCalcDataSlave]
    def name = "clac-data-slave"
}

class alCalcDataSlave extends Actor with ActorLogging {

    import scala.concurrent.ExecutionContext.Implicits.global
    case class state_agent(val isRunning : Boolean)
    val stateAgent = Agent(state_agent(false))

    override def receive: Receive = {
        case calc_data_hand() => if (stateAgent().isRunning) Unit
        else {
            stateAgent send state_agent(true)
            val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
            a ! takeNodeForRole("splitcalcslave")
            sender ! calc_data_hand()
        }
        case calc_data_start_impl(lsp, c) => {
            println(s"property are ${lsp}")
            val cur = context.actorOf(alCalcDataComeo.props(c, lsp, sender, self))
            cur.tell(calc_data_start_impl(lsp, c), sender)
        }
        case cmd : calc_data_end => {
            val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
            a ! refundNodeForRole("splitcalcslave")
            stateAgent send state_agent(false)
        }
    }
}