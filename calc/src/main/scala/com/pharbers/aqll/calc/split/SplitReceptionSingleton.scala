package com.pharbers.aqll.calc.split

import akka.actor._
import akka.cluster.singleton.ClusterSingletonManager
import akka.cluster.singleton.ClusterSingletonManagerSettings
import akka.cluster.singleton.ClusterSingletonProxy
import akka.cluster.singleton.ClusterSingletonProxySettings
import com.pharbers.aqll.calc.maxmessages.{ excelJobStart, freeMaster, registerMaster }

object SplitReceptionSingleton {
    def props = Props(new SplitReceptionSingleton)
//    def name = "reception-singleton"
    def name = "splitreception"
}

class SplitReceptionSingleton extends Actor {
    val singletonManager = context.system.actorOf(
        ClusterSingletonManager.props(
            SplitReception.props,
            PoisonPill,
            ClusterSingletonManagerSettings(context.system)
                .withRole(None)
                .withSingletonName(SplitReception.name)
        )
    )

    val reception = context.system.actorOf(
        ClusterSingletonProxy.props(
            singletonManager.path.child(SplitReception.name)
                .toStringWithoutAddress,
            ClusterSingletonProxySettings(context.system)
                .withRole(None)
                .withSingletonName("reception-proxy")
        )
    )

    def receive = {
        case cmd : excelJobStart => reception forward cmd
        case cmd : freeMaster => reception forward cmd
        case cmd : registerMaster => reception forward cmd
    }
}
