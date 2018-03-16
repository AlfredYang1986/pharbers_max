package com.pharbers.aqll.alStart.alEntry

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

object alDeliverySlaveEntry extends App {
	val config = ConfigFactory.load("split-delivery-file-slave")
	val system = ActorSystem("calc", config)
}
