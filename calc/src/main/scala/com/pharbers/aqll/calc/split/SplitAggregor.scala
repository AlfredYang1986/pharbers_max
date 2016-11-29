package com.pharbers.aqll.calc.split

import akka.actor.Actor
import akka.event.EventBus
import akka.actor.ActorLogging
import akka.actor.Props
import akka.event.ActorEventBus
import akka.event.LookupClassification

object SplitAggregor {
    def props(msgSize: Int) = Props(new SplitAggregor(msgSize))
}

case class ave(sum1: Double, sum2: Double)

class SplitAggregor(msgSize: Int) extends Actor with EventBus with LookupClassification with ActorEventBus with ActorLogging{
    
    def idel: Receive = {
        case ave(sum1, sum2) => {
            println(s"sum1 = ${sum1},sum2 = ${sum2}")
        }
        
        case _ => ???
    }
    
    type Event = AnyRef
    
    override def mapSize = msgSize
    
    type Classifier = String
    
    override def classify(event: Event) = {
        "AggregorBus"
    }
    
    override def publish(event: Event, subscriber:Subscriber) {
        subscriber ! event
    }
    
    def subscribe(subscriber: Subscriber): Boolean = subscribers.put("AggregorBus", subscriber)
    
    def receive = idel
}