package com.pharbers.aqll.alStart.alEntry

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

object alAggregationSlaveEntry extends App {
	val config = ConfigFactory.load("split-aggregation-data-slave")
	val system = ActorSystem("calc", config)
}
