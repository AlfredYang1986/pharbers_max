package com.pharbers.aqll.alMSA.alCalcAgent

import akka.actor.{Actor, ActorLogging, Props}
import akka.routing.RoundRobinPool
import com.pharbers.alCalcMemory.alprecess.alsplitstrategy.server_info
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcData.{calc_data_end, calc_data_result}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoRestoreBson.restore_bson_end
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxMaster
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxMaster._
import com.pharbers.driver.redis.phRedisDriver

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

    // val master_router = context.actorOf(RoundRobinPool(1).props(alMaxMaster.props), alMaxMaster.name)

    val master_router = context.actorOf(
            ClusterRouterPool(RoundRobinPool(1),
                ClusterRouterPoolSettings(
                    totalInstances = 1,
                    maxInstancesPerNode = 1,
                    allowLocalRoutees = true,
                    useRole = Some("splitmaster")
                )
            ).props(alMaxMaster.props), alMaxMaster.name)

    val core_number: Int = server_info.cpu

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



        // case msg: pushCalcYMJob => master_router forward msg

        // case msg: pushGeneratePanelJob => master_router forward msg
        // case msg: generatePanelResult => master_router forward msg

        // case msg: pushSplitPanelJob => println(s"alPropertyAgent.pushSplitPanelJob"); master_router forward msg

        case sumCalcJob(items, s) => {
            // TODO: 现在单机多线程情况,需要时再写多机器多线
            // TODO: 因为是在Master结点上,所以改一下判断依据即可
            val redisDriver = phRedisDriver().commonDriver
            var sum = redisDriver.get(s"Uid${items.uid}calcSum").get.toInt
            sum += 1
            redisDriver.set(s"Uid${items.uid}calcSum", sum)
            if(sum == core_number){
                master_router ! sumCalcJob(items, s)
                sum = 0
            }
        }
        // case msg: calc_data_end => master_router forward msg

        // case msg: restore_bson_end => master_router forward msg

         case msg: AnyRef => master_router forward msg
    }

}
