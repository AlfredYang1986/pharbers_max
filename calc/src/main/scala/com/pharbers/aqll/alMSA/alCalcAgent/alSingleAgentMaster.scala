package com.pharbers.aqll.alMSA.alCalcAgent

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger._
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.queryEnergy
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by jeorch on 17-8-8.
  * The SingleAgentMaster is used to determine whether the machine's calculate energy is available.
  */

object alSingleAgentMaster {
    def props = Props[alSingleAgentMaster]
    def name = "single-agent"

//    case class latestEnergy(energy : Map[String,Int])
//    case class query()
}

class alSingleAgentMaster extends Actor with ActorLogging {

//    import alSingleAgentMaster._
//    implicit val timeout = Timeout(1 minute)

//    val energyAgent = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")


    override def receive: Receive = {

        case _ => ???

//        case query() => {
//
//            val f = energyAgent ? queryEnergy()
//            val energy = Await.result(f, 1 seconds).asInstanceOf[latestEnergy].energy
//            if(energy.size == energy.values.filterNot(x => x==0).size) sender() ! true
//            else sender() ! false
//        }
    }
}


