package com.pharbers.aqll.alStart.alEntry

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

/**
  * Created by alfredyang on 12/07/2017.
  */
object alSplitPanelSlaveEntry extends App {
    val config = ConfigFactory.load("split-split-panel-slave")
    val system = ActorSystem("calc", config)
}
