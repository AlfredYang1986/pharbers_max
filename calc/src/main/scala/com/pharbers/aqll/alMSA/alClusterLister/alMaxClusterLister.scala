package com.pharbers.aqll.alMSA.alClusterLister

import akka.actor.{Actor, ActorLogging}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.{refundNodeForRole, takeNodeForRole}

/**
  * Created by alfredyang on 11/07/2017.
  */
class alMaxClusterLister extends Actor with ActorLogging {

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
            val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
            member.roles.map (x => a ! refundNodeForRole(x))
        }

        case MemberRemoved(member, previousStatus) => {
            val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
            member.roles.map (x => a ! takeNodeForRole(x))
        }
    }
}
