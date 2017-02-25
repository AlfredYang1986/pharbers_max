package com.pharbers.aqll.calc.Http

import scala.concurrent.Future
import akka.actor.{ActorSystem, Props}
import akka.cluster.Cluster
import akka.util.Timeout
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding

import akka.stream.ActorMaterializer
import com.pharbers.aqll.calc.split.{ EventCollector, SplitReceptionSingleton }
import com.typesafe.config.{Config, ConfigFactory}

/**
  * Created by Faiz on 2017/1/7.
  */

object CheckGloble {
	var system : akka.actor.ActorSystem = null
}

object CheckExcelHttpApp extends App with RequestTimeout {
    val config = ConfigFactory.load("application")
    val host = config.getString("http.host")
    val port = config.getInt("http.port")

    implicit val system = ActorSystem("CheckMain")
    implicit val ec = system.dispatcher

    val api = new OrderServiceApi(system, requestTimeout(config)).routes

    implicit val materializer = ActorMaterializer()
    val bindingFuture: Future[ServerBinding] = Http().bindAndHandle(api, host, port)

    bindingFuture.map { serverBinding =>
        stubmain
        println(s"serverBinding = ${serverBinding}")
    }.onFailure {
        case ex: Exception =>
            system.terminate()
    }
    def stubmain = {
        import scala.concurrent.duration._

        val config = ConfigFactory.load("split-master")
	    val system = ActorSystem("calc", config)
        ActorSystem("queue").scheduler.schedule(1 seconds, 2 seconds , system.actorOf(QueueActor.props), ThreadQueue())
        val node_ip = system.settings.config.getStringList("akka.cluster.seed-nodes")
	    val a = system.actorOf(SplitReceptionSingleton.props, SplitReceptionSingleton.name)
	    println(s"singleton is $a")
        if(system.settings.config.getStringList("akka.cluster.roles").contains("splitmaster")) {
            Cluster(system).registerOnMemberUp {
//                system.actorOf(SplitReception.props, SplitReception.name)//"splitreception"
//                system.actorOf(SplitReceptionSingleton.props, SplitReceptionSingleton.name)
	            CheckGloble.system = system
            }
            system.actorOf(Props(new EventCollector), "cluster-listener")
        }
    }
}



trait RequestTimeout {
    import scala.concurrent.duration._
    def requestTimeout(config: Config): Timeout = {
        val t = config.getString("akka.http.server.request-timeout")
        val d = Duration(t)
        FiniteDuration(d.length, d.unit)
    }
}
