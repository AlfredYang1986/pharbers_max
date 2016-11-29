package com.pharbers.aqll.calc.split

import akka.event.LookupClassification
import akka.actor.ActorLogging
import akka.event.EventBus
import akka.event.ActorEventBus

sealed class broadcastingDefines

object SplitEventBus {
	case class excelEnded() extends broadcastingDefines 
	case class average(ave1 : Double, ave2 : Double) extends broadcastingDefines 
}

class SplitEventBus(s : Int) extends EventBus with LookupClassification with ActorEventBus {
    type Event = broadcastingDefines
    override def mapSize = s
    type Classifier = String
    
    override def classify(event : Event) = {
        "AggregorBus"
    }
    
    override def publish(event : Event, subscriber : Subscriber) {
        subscriber ! event
    }
    
    def subscribe(subscriber : Subscriber) : Boolean = subscribers.put("AggregorBus", subscriber)
 
}