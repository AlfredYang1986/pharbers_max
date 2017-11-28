package com.pharbers.aqll.alMSA.alCalcAgent

import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.routing.{BroadcastPool, RoundRobinPool}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcData.{calc_data_end, calc_data_result}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoRestoreBson.restore_bson_end
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxMaster
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxMaster._

/**
  * Created by alfredyang on 11/07/2017.
  */
object alPropertyAgent {
    def props = Props[alPropertyAgent]
    def name = "agent-actor"

    case class queryIdleNodeInstanceInSystemWithRole(role : String)
    case class takeNodeForRole(role : String)
    case class refundNodeForRole(role : String)
}

class alPropertyAgent extends Actor with ActorLogging {

    var energy : Map[String, Int] = Map("splitmaster" -> 0,
                                        "splitgeneratepanelslave" -> 0,
                                        "splitcalcymslave" -> 0,
                                        "splitsplitpanelslave" -> 0,
                                        "splitgroupslave" -> 0,
                                        "splitcalcslave" -> 0,
                                        "splitrestorebsonslave" -> 0,
                                        "splittest" -> 0)

    val master_router = context.actorOf(BroadcastPool(1).props(alMaxMaster.props), alMaxMaster.name)

    import alPropertyAgent._
    override def receive: Receive = {
        case queryIdleNodeInstanceInSystemWithRole(role) => {
            // println(s"queryIdleNodeInstanceInSystemWithRole.energy=${energy}")
            sender ! energy.get(role).map (x => x).getOrElse(-1)
        }
        case takeNodeForRole(role) => {
            val f = energy.find(role == _._1)
            val can = f.map (_._2 > 0).getOrElse(false)
            if (can) {
                energy = energy.filterNot(x => x._1 == role) + (role -> (f.get._2 - 1))
                // println(s"takeNodeForRole.energy=${energy}")
                sender ! true
            } else sender ! false
        }
        case refundNodeForRole(role) => {
            val f = energy.find(role == _._1)
            energy = energy.filterNot(x => x._1 == role) + (role -> (f.get._2 + 1))
            // println(s"refundNodeForRole.energy=${energy}")
            sender ! true
        }

        case msg: pushCalcYMJob => master_router forward msg

        case msg: pushGeneratePanelJob => master_router forward msg
        case msg: generatePanelResult => master_router forward msg

        case msg: pushSplitPanelJob => {
            println(s"&& alPropertyAgent => pushSplitPanelJob")
            master_router forward msg
        }

        case sumCalcJob(item, s) => {
            println(s"&& alPropertyAgent => sumCalcJob")
            master_router ! sumCalcJob(item, s)
        }
        case calc_data_result(v, u) => master_router ! calcDataResult(v, u)
        case msg: calc_data_end => master_router forward msg

        case msg: restore_bson_end => master_router forward msg

        case msg: AnyRef => log.info(s"Error Message[${msg}] not deliver!")
    }

}
