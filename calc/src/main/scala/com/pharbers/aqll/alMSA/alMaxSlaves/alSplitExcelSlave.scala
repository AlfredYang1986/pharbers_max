package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props, SupervisorStrategy}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.{refundNodeForRole, takeNodeForRole}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoSplitExcel.{split_excel_end, split_excel_hand, split_excel_start_impl}

import scala.concurrent.Await

/**
  * Created by alfredyang on 12/07/2017.
  */

object alSplitExcelSlave {
    def props = Props[alSplitExcelSlave]
    def name = "split-excel-slave"

//    import scala.concurrent.ExecutionContext.Implicits.global
//    case class slave_status(val canDoJob : Boolean)
//    val slaveStatus = Agent(slave_status(true))
//
//    case class state_agent(val isRunning : Boolean)
//    val stateAgent = Agent(state_agent(false))
}

class alSplitExcelSlave extends Actor with ActorLogging {
    override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
        case _ => Restart
    }

    override def receive: Receive = {
        case split_excel_hand() => {
                                        implicit val t = Timeout(2 seconds)
                                        val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
                                        val f = a ? takeNodeForRole("splitsplitexcelslave")
                                        if (Await.result(f, t.duration).asInstanceOf[Boolean]) sender ! split_excel_hand()
                                        else Unit
                                   }
        case split_excel_start_impl(file, parmary) => {
            val counter = context.actorOf(alCommonErrorCounter.props)
            val cur = context.actorOf(alSplitExcelComeo.props(file, parmary, sender, self, counter))
            cur.tell(split_excel_start_impl(file, parmary), sender)
        }
        case cmd : split_excel_end => {
            val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
            a ! refundNodeForRole("splitsplitexcelslave")
        }
    }}
