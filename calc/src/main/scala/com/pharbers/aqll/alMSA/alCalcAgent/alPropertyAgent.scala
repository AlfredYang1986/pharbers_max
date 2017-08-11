package com.pharbers.aqll.alMSA.alCalcAgent

import akka.actor.{Actor, ActorLogging, Props}
import akka.agent.Agent
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger._

import scala.concurrent.duration._
import akka.util.Timeout
import akka.pattern.ask

import scala.concurrent.Await

/**
  * Created by alfredyang on 11/07/2017.
  */
object alPropertyAgent {
    def props = Props[alPropertyAgent]
    def name = "agent-actor"

    case class queryIdleNodeInstanceInSystemWithRole(role : String)
    case class takeNodeForRole(role : String)
    case class refundNodeForRole(role : String)

    case class queryEnergy()

}

class alPropertyAgent extends Actor with ActorLogging {

    var energy : Map[String, Int] = Map("splitmaster" -> 0,
                                        "splitfilterexcelslave" -> 0,
                                        "splitsplitexcelslave" -> 0,
                                        "splitgroupslave" -> 0,
                                        "splitcalcslave" -> 0,
                                        "splittest" -> 0)
    import alPropertyAgent._
    override def receive: Receive = {
        case queryIdleNodeInstanceInSystemWithRole(role) =>
            //println(s"&&&queryIdleNodeInstanceInSystemWithRole&&&=${energy}")
            sender ! energy.get(role).map (x => x).getOrElse(-1)
        case takeNodeForRole(role) => {
            val f = energy.find(role == _._1)
//            println(s"&查看& energy.find(role) = ${f}")
            val can = f.map (_._2 > 0).getOrElse(false)
            if (can) {
                energy = energy.filterNot(x => x._1 == role) + (role -> (f.get._2 - 1))
//                println(s"&&& 可以计算 占用一个算能后 takeNodeForRole&&&=${energy}")
                sender ! true
            } else /*println(s"&&* 算能${role}资源正在被占用!!请耐心等待... *&&");*/sender ! false
        }
        case refundNodeForRole(role) => {
            val f = energy.find(role == _._1)
//            println(s"&& 查看 refundNodeForRole & energy.find(role) = ${f}")
            energy = energy.filterNot(x => x._1 == role) + (role -> (f.get._2 + 1))
//            println(s"&&& 重置算能后 refundNodeForRole&&&=${energy}")
            sender ! true
        }
//        case queryEnergy() => {
//            sender() ! latestEnergy(energy)
//        }

    }
}
