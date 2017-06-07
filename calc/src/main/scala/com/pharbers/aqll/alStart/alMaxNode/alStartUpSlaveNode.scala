package com.pharbers.aqll.alStart.alMaxNode

import akka.actor.ActorSystem
import akka.cluster.Cluster
import com.pharbers.aqll.alCalc.almain.{alCalcActor, alGroupActor}
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger.{calc_register, group_register, worker_register}
import com.pharbers.aqll.alCalcOther.alRemoveJobs.{alScheduleRemoveFiles, rmFile}
import com.pharbers.aqll.common.alFileHandler.clusterListenerConfig.singletonPaht
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
                                val a = system.actorSelection(singletonPaht)
                                val c = system.actorOf(alCalcActor.props)
                                val w = system.actorOf(alGroupActor.props)
                                a ! group_register(w)
                                a ! calc_register(c)
                                a ! worker_register()
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
