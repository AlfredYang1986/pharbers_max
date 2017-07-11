package com.pharbers.aqll.alMaxSlaves

import akka.actor.{Actor, ActorLogging, Props}

/**
  * Created by alfredyang on 11/07/2017.
  */

object alFilterExcelSlave {
    def props = Props[alFilterExcelSlave]
    def name = "filter-excel-slave"
}

class alFilterExcelSlave extends Actor with ActorLogging {

}
