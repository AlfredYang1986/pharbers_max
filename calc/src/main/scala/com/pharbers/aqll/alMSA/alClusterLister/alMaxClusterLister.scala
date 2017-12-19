package com.pharbers.aqll.alMSA.alClusterLister

import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.actor.{Actor, ActorLogging}
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.{refundNodeForRole, takeNodeForRole}

/**
  * Created by alfredyang on 11/07/2017.
  */
class alMaxClusterLister extends Actor with ActorLogging {
    val cluster = Cluster(context.system)
    
    override def preStart() = {
        cluster.subscribe(self, initialStateMode = InitialStateAsEvents,classOf[MemberUp], classOf[MemberRemoved])
    }
    
    override def postStop() = {
        cluster.unsubscribe(self)
    }
    
    override def receive = {
        case MemberJoined(member) => log.info(s"Member $member Joined")
        
        case MemberUp(member) => {
            log.info(s"MemberUp => $member !!!")
            val a = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
            member.roles.foreach (x => a ! refundNodeForRole(x))
        }
        
        case MemberRemoved(member, _) => {
            log.info(s"MemberRemoved => $member !!!")
            val a = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
            member.roles.foreach (x => a ! takeNodeForRole(x))
        }
    }
}
