package com.pharbers.aqll.alStart.alEntry

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

/**
  * Created by alfredyang on 13/07/2017.
  */
object alGroupSlaveEntryAnother extends App {
    val config = ConfigFactory.load("split-group-slave-another")
    val system = ActorSystem("calc", config)
}
