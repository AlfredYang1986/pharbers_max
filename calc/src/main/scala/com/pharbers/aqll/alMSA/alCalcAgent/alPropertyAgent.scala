package com.pharbers.aqll.alMSA.alCalcAgent

import akka.actor.{Actor, ActorLogging, Props}

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
                                        "splitfilterexcelslave" -> 0,
                                        "splitsplitexcelslave" -> 0,
                                        "splitgroupslave" -> 0,
                                        "splitcalcslave" -> 0,
                                        "splitrestorebsonslave" -> 0,
                                        "splittest" -> 0)
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
    }

}
