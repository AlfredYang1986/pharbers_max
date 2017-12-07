package com.pharbers.aqll.alMSA.alClusterLister

import akka.actor.{Actor, ActorLogging}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.{refundNodeForRole, takeNodeForRole}

import com.typesafe.config.ConfigFactory

/**
  * Created by alfredyang on 11/07/2017.
  */
class alMaxClusterLister extends Actor with ActorLogging {
    
    val cluster = Cluster(context.system)
    val masterIP = ConfigFactory.load("split-new-master").getString("akka.remote.netty.tcp.hostname")
    
    override def preStart() = {
        cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
            classOf[MemberUp], classOf[MemberRemoved], classOf[UnreachableMember], classOf[ReachableMember])
    }
    
    override def postStop() = {
        cluster.unsubscribe(self)
    }
    
    override def receive = {
        case MemberJoined(member) => log.info(s"Member ${member} Joined")
        
        case MemberUp(member) => {
            log.info(s"MemberUp => ${member} !!!")
            val a = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
            member.roles.map (x => a ! refundNodeForRole(x))
        }
        
        case MemberRemoved(member, previousStatus) => {
            log.info(s"MemberRemoved => ${member} !!!")
            val a = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
            member.roles.map (x => a ! takeNodeForRole(x))
        }

        case UnreachableMember(member) => {
            log.info(s"UnreachableMember => ${member} !!!")
        }

        case ReachableMember(member) => {
            log.info(s"ReachableMember => ${member} !!!")
        }
    }

}
