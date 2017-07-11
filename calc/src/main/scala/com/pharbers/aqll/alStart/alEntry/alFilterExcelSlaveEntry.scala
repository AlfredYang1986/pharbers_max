package com.pharbers.aqll.alStart.alEntry

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

/**
  * Created by alfredyang on 11/07/2017.
  */
object alFilterExcelSlaveEntry extends App {
    val config = ConfigFactory.load("split-filter-excel-slave")
    val system = ActorSystem("calc", config)
}
