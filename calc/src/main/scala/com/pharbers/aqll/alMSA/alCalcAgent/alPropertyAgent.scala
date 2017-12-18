package com.pharbers.aqll.alMSA.alCalcAgent

import akka.routing.RoundRobinPool
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxMaster
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import com.pharbers.aqll.alCalaHelp.alLog.alTempLog

/**
  * Created by alfredyang on 11/07/2017.
  */
object alPropertyAgent {
    def props: Props = Props[alPropertyAgent]
    def name = "agent-actor"

    case class queryIdleNodeInstanceInSystemWithRole(role : String)
    case class takeNodeForRole(role : String)
    case class refundNodeForRole(role : String)
}

class alPropertyAgent extends Actor with ActorLogging {

    var energy : Map[String, Int] = Map("splitmaster" -> 0,
                                        "splitcalcymslave" -> 0,
                                        "splitgeneratepanelslave" -> 0,
                                        "splitsplitpanelslave" -> 0,
                                        "splitgroupslave" -> 0,
                                        "splitcalcslave" -> 0,
                                        "splitrestorebsonslave" -> 0,
                                        "splittest" -> 0)

    val master_router: ActorRef = context.actorOf(
            ClusterRouterPool(RoundRobinPool(1),
                ClusterRouterPoolSettings(
                    totalInstances = 1,
                    maxInstancesPerNode = 1,
                    allowLocalRoutees = true,
                    useRole = Some("splitmaster")
                )
            ).props(alMaxMaster.props), alMaxMaster.name)

    import alPropertyAgent._
    override def receive: Receive = {
        case queryIdleNodeInstanceInSystemWithRole(role) => {
            sender ! energy.get(role).map (x => x).getOrElse(-1)
        }

        case takeNodeForRole(role) => {
            val f = energy.find(role == _._1)
            val can = f.map (_._2 > 0).getOrElse(false)
            if (can) {
                energy = energy.filterNot(_._1 == role) + (role -> (f.get._2 - 1))
                alTempLog(s"take ${role} Node, Now Energy = ${f.get._2 - 1} ")
                sender ! true
            } else sender ! false
        }

        case refundNodeForRole(role) => {
            val f = energy.find(role == _._1)
            energy = energy.filterNot(x => x._1 == role) + (role -> (f.get._2 + 1))
            alTempLog(s"refund ${role} Node, Now Energy = ${f.get._2 + 1} ")
            sender ! true
        }

        case msg: AnyRef => master_router forward msg
    }

}
