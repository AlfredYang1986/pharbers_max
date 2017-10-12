package com.pharbers.aqll.alStart.alEntry

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

/**
  * Created by jeorch on 17-10-11.
  */
object alGeneratePanelSlaveEntry extends App {
    val config = ConfigFactory.load("split-generate-panel-slave")
    val system = ActorSystem("calc", config)
}
