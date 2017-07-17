package com.pharbers.aqll.alMSA.alCalcAgent

import akka.actor.{Actor, ActorLogging, PoisonPill, Props}
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings, ClusterSingletonProxy, ClusterSingletonProxySettings}
import com.pharbers.aqll.alCalc.almain.alMaxDriver

/**
  * Created by alfredyang on 11/07/2017.
  */

object alAgentSingleton {
    def props = Props(new alAgentSingleton)
    def name = "agent-reception"
}

class alAgentSingleton extends Actor with ActorLogging {
    val singletonManager = context.system.actorOf(
        ClusterSingletonManager.props(
            alPropertyAgent.props,
            PoisonPill,
            ClusterSingletonManagerSettings(context.system)
                .withRole(Some("splitmaster"))
                .withSingletonName("agent-actor")
        ), name = "agent-master"
    )

    val driver = context.system.actorOf(
        ClusterSingletonProxy.props(
            singletonManager.path.toStringWithoutAddress,
            ClusterSingletonProxySettings(context.system)
                .withRole(Some("splitmaster"))
                .withSingletonName("agent-actor")
        ), "agent-actor"
    )

    def receive = {
        case cmd => driver forward cmd
    }
}
