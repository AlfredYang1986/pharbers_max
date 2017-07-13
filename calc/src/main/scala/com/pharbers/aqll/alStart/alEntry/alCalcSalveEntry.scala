package com.pharbers.aqll.alStart.alEntry

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

/**
  * Created by alfredyang on 13/07/2017.
  */
object alCalcSalveEntry extends App {
    val config = ConfigFactory.load("split-calc-slave")
    val system = ActorSystem("calc", config)
}
