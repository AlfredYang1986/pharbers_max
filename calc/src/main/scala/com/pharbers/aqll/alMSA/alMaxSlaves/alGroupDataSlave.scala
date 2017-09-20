package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props, SupervisorStrategy}
import akka.agent.Agent
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.{refundNodeForRole, takeNodeForRole}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoGroupData.{group_data_end, group_data_hand, group_data_start_impl}
import com.pharbers.aqll.alMSA.alMaxCmdMessage.{alCmdActor, unpkgend}

/**
  * Created by alfredyang on 12/07/2017.
  */
object alGroupDataSlave {
    def props = Props[alGroupDataSlave]
    def name = "group-data-slave"

    import scala.concurrent.ExecutionContext.Implicits.global
    case class state_agent(val isRunning : Boolean)
    val stateAgent = Agent(state_agent(false))

    case class slave_status(val canDoJob : Boolean)
    val slaveStatus = Agent(slave_status(true))
}

class alGroupDataSlave extends Actor with ActorLogging {

    import alGroupDataSlave._

    override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
        case _ => Restart
    }
    
    def cmdActor = context.actorOf(alCmdActor.props())

    override def receive: Receive = {
        case group_data_hand() => if (stateAgent().isRunning) Unit
        else {
            // TODO: 发送解压消息 解压Sync文件
//            cmdActor ! unpkgmsg(s"${root}${scpPath}${property.uuid}", s"${root}${program}")
            stateAgent send state_agent(true)
            val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
            a ! takeNodeForRole("splitgroupslave")
            sender ! group_data_hand()
        }
        case unpkgend(s) => {
            // TODO: 销毁解压消息
            context stop s
            sender ! group_data_hand()
        }
        case group_data_start_impl(sp) => {
            val counter = context.actorOf(alCommonErrorCounter.props)
            val cur = context.actorOf(alGroupDataComeo.props(sp, sender, self, counter))
            cur.tell(group_data_start_impl(sp), sender)
        }
        case cmd : group_data_end => {
            val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
            a ! refundNodeForRole("splitgroupslave")
            stateAgent send state_agent(false)
        }
    }
}
