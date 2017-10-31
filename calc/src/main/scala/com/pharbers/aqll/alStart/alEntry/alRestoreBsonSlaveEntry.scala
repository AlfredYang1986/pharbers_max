package com.pharbers.aqll.alStart.alEntry

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

/**
  * Created by jeorch on 17-10-30.
  */
object alRestoreBsonSlaveEntry extends App {
    val config = ConfigFactory.load("split-restore-bson-slave")
    val system = ActorSystem("calc", config)
}
