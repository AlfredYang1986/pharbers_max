package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.actor.{Actor, ActorLogging, Props}
import akka.agent.Agent
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.{refundNodeForRole, takeNodeForRole}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoSplitExcel.{split_excel_end, split_excel_hand, split_excel_start_impl}

/**
  * Created by alfredyang on 12/07/2017.
  */

object alSplitExcelSlave {
    def props = Props[alSplitExcelSlave]
    def name = "split-excel-slave"
}

class alSplitExcelSlave extends Actor with ActorLogging {

    import scala.concurrent.ExecutionContext.Implicits.global
    case class state_agent(val isRunning : Boolean)
    val stateAgent = Agent(state_agent(false))

    override def receive: Receive = {
        case split_excel_hand() => if (stateAgent().isRunning) Unit
                                   else {
                                        stateAgent send state_agent(true)
                                        val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
                                        a ! takeNodeForRole("splitsplitexcelslave")
                                        sender ! split_excel_hand()
                                   }
        case split_excel_start_impl(file, parmary) => {
            val cur = context.actorOf(alSplitExcelComeo.props(file, parmary, sender, self))
            context.watch(cur)
            cur.tell(split_excel_start_impl(file, parmary), sender)
        }
        case cmd : split_excel_end => {
            val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
            a ! refundNodeForRole("splitsplitexcelslave")
            stateAgent send state_agent(false)
        }
    }}
