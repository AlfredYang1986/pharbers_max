package com.pharbers.aqll.alStart.alEntry

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

/**
  * Created by alfredyang on 12/07/2017.
  */
object alSplitExcelSlaveEntry extends App {
    val config = ConfigFactory.load("split-split-excel-slave")
    val system = ActorSystem("calc", config)
}
