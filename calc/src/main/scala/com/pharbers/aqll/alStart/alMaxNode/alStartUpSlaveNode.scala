package com.pharbers.aqll.alStart.alMaxNode

import akka.actor.ActorSystem
import akka.cluster.Cluster
import com.pharbers.aqll.alCalcEnergy.{alCalcRegisterActor, alGroupRegisterActor}
import com.pharbers.aqll.alCalcOther.alRemoveJobs.{alScheduleRemoveFiles, rmFile}
import com.typesafe.config.ConfigFactory

/**
  * Created by liwei on 2017/6/7.
  */
object alStartUpSlaveNode extends App{
    try {
        val hostOpt: Option[String] = Some(System.getProperty("host"))
        hostOpt match {
            case None => throw new Exception("hostname is null")
            case Some(host) => {
                val portOpt: Option[String] = Some(System.getProperty("port"))
                portOpt match {
                    case None => throw new Exception("port is null")
                    case Some(port) => {
                        val config = ConfigFactory.load("split-slave")
                        val replacementConfig = ConfigFactory.parseString(s"akka{remote {netty.tcp {hostname = ${host},port = ${port}}}}")
                        val system = ActorSystem("calc", replacementConfig.withFallback(config))

                        if (system.settings.config.getStringList("akka.cluster.roles").contains("splitworker")) {
                            Cluster(system).registerOnMemberUp {
                                import scala.concurrent.duration._
                                import scala.concurrent.ExecutionContext.Implicits.global
                                system.actorOf(alGroupRegisterActor.props, "registergroup")
                                system.actorOf(alCalcRegisterActor.props, "registercalc")
                                val rm = system.actorOf(alScheduleRemoveFiles.props)
                                system.scheduler.schedule(0 seconds, 10 seconds, rm, new rmFile())
                            }
                        }
                    }
                }
            }
        }
    } catch {
        case ex: Exception => println(ex.getMessage)
    }
}
