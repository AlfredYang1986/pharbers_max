package com.pharbers.aqll.alMSA.alClusterLister

import com.typesafe.config.ConfigFactory

/**
  * Created by clock on 17-12-18.
  */
object alAgentIP {
    val masterIP = ConfigFactory.load("split-new-master").getString("akka.remote.netty.tcp.hostname")
}
