package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.actor.{Actor, ActorLogging, Props}
import akka.agent.Agent
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.{refundNodeForRole, takeNodeForRole}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoFilterExcel.{filter_excel_end, filter_excel_hand, filter_excel_start_impl}

/**
  * Created by alfredyang on 11/07/2017.
  */

object alFilterExcelSlave {
    def props = Props[alFilterExcelSlave]
    def name = "filter-excel-slave"
}

class alFilterExcelSlave extends Actor with ActorLogging {

    import scala.concurrent.ExecutionContext.Implicits.global
    case class state_agent(val isRunning : Boolean)
    val stateAgent = Agent(state_agent(false))

    override def receive: Receive = {
        case filter_excel_hand() => if (stateAgent().isRunning) Unit
                                    else {
                                        stateAgent send state_agent(true)
                                        val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
                                        a ! takeNodeForRole("splitfilterexcelslave")
                                        sender ! filter_excel_hand()
                                    }
        case filter_excel_start_impl(file, parmary) => {
            val cur = context.actorOf(alFilterExcelComeo.props(file, parmary, sender, self))
            context.watch(cur)
            cur.tell(filter_excel_start_impl(file, parmary), sender)
        }
        case cmd : filter_excel_end => {
            val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
            a ! refundNodeForRole("splitfilterexcelslave")
            stateAgent send state_agent(false)
        }
    }
}




