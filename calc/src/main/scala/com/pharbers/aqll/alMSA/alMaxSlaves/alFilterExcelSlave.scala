package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props, SupervisorStrategy}
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

    import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoFilterExcel._

    override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
        case _ => Restart
    }

    override def receive: Receive = {
        case filter_excel_hand() => {
//            println(s"接收到从Trail发来的filter_excel_hand命令 ##stateAgent结果 = ${stateAgent().isRunning}##")
            if (stateAgent().isRunning) {
                Unit
            } else {
                stateAgent send state_agent(true)
                val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
                a ! takeNodeForRole("splitfilterexcelslave")
                sender ! filter_excel_hand()
            }
        }
        case filter_excel_start_impl(file, parmary) => {
            //println("执行 filter_excel_start_impl ##")
            val counter = context.actorOf(alCommonErrorCounter.props)
            val cur = context.actorOf(alFilterExcelComeo.props(file, parmary, sender, self, counter))
            //println(s"创建 counter = $counter ##\n创建 alFilterExcelComeo = $cur ##")
            //println(s"Slave 下的 self = ${self}#即为Comeo的owner")
//            context.watch(cur)
            cur.tell(filter_excel_start_impl(file, parmary), sender)
        }
        case cmd : filter_excel_end => {
            //println("结束 Slave - filter_excel_end")
            val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
            a ! refundNodeForRole("splitfilterexcelslave")
            stateAgent send state_agent(false)
            //println("释放 refundNodeForRole => splitfilterexcelslave ")
        }
    }
}




