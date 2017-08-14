package com.pharbers.aqll.alMSA.alClusterLister

import akka.actor.{Actor, ActorLogging, Address}
import akka.agent.Agent
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.{refundNodeForRole, takeNodeForRole}

import scala.concurrent.stm.Ref
import scala.concurrent.stm.atomic
import scala.concurrent.ExecutionContext.Implicits.global

object alMaxAgentEnergy {
    val agentEnergy = Ref(List[Address]())
}

/**
  * Created by alfredyang on 11/07/2017.
  */
class alMaxClusterLister extends Actor with ActorLogging {
    import alMaxAgentEnergy._
    
    val cluster = Cluster(context.system)
    
    override def preStart() = {
        cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
            classOf[MemberUp], classOf[MemberRemoved], classOf[UnreachableMember], classOf[ReachableMember])
    }
    
    override def postStop() = {
        cluster.unsubscribe(self)
    }
    
    override def receive = {
        case MemberJoined(member) => log.info("Member Joined")
        
        case MemberUp(member) => {
            if(member.roles.contains("splitmaster")) {
                atomic { implicit thx =>
                    agentEnergy() = agentEnergy() :+ member.address
                }
            }
            val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
//            val a = context.actorSelection(s"akka.tcp://${agentEnergy.single.get.find(x => x.host.get == member.address.host.get).getOrElse(member.address.hostPort)}/user/agent-reception")
            member.roles.map (x => a ! refundNodeForRole(x))
        }
        
        case MemberRemoved(member, previousStatus) => {
            val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
//            val a = context.actorSelection(s"akka.tcp://${agentEnergy.single.get.find(x => x.host.get == member.address.host.get).getOrElse(member.address.hostPort)}/user/agent-reception")
            member.roles.map (x => a ! takeNodeForRole(x))
            
            if(member.roles.contains("splitmaster")) {
                atomic { implicit thx =>
                    agentEnergy() = agentEnergy().filterNot(x => x == member.address)
                }
            }
        }
    }
}
