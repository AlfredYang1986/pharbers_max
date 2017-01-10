package scala.com.pharbers.aqll.calc.check

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/**
  * Created by Faiz on 2017/1/4.
  */
object CheckExcel extends App{
    val conifg = """akka{
                loglevel = DEBUG
                stdout-loglevel = WARNING
                loggers = ["akka.event.slf4j.Slf4jLogger"]
                actor {
                  provider = "akka.remote.RemoteActorRefProvider"
                }
                remote {
                  enabled-transports = ["akka.remote.netty.tcp"]
                  netty.tcp {
                    hostname = "127.0.0.1"
                    port = 4771
                  }
                }
            }"""

    val conf = ConfigFactory.parseString(conifg)
    val backend = ActorSystem("ExcelMain", conf)
    backend.actorOf(CheckReception.props, "sample")
}
