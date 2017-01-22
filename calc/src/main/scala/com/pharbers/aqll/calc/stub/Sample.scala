package com.pharbers.aqll.calc.stub

import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorSystem
import com.pharbers.aqll.calc.split.SplitReception
import akka.actor.ActorRef

object Sample {
    def props = Props(new Sample())
}

class Sample() extends Actor{
    def receive = {
        case "cpamarket" => Unit
        case "cpaproduct" => println("in cpaproduct");context.system.terminate()//new StubWorkerMain()
        case "phamarket" => Unit
        case "phaproduct" => Unit
    }
    
}