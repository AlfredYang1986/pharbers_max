package scala.com.pharbers.aqll.calc.Http

import scala.concurrent.Future
import akka.actor.{Actor, ActorSystem, Props}
import akka.cluster.Cluster
import akka.event.Logging
import akka.util.Timeout
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.pharbers.aqll.calc.Http.{QueueActor, ThreadQueue}
import com.pharbers.aqll.calc.split.{ClusterEventListener, EventCollector, SplitReception}
import com.pharbers.aqll.calc.stub.AkkaReception
import com.pharbers.aqll.calc.util.{ListQueue}
import com.typesafe.config.{Config, ConfigFactory}

/**
  * Created by Faiz on 2017/1/7.
  */

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
//        val conf = """akka {
//					  loglevel = DEBUG
//					  stdout-loglevel = WARNING
//					  loggers = ["akka.event.slf4j.Slf4jLogger"]
//
//					  actor {
//					    provider = "akka.remote.RemoteActorRefProvider"
//					  }
//
//					  remote {
//					    enabled-transports = ["akka.remote.netty.tcp"]
//					    netty.tcp {
//					      hostname = "127.0.0.1"
//					      port = 3550
//					    }
//					  }
//					}
//              """
//
//        val configr = ConfigFactory.parseString(conf)
//        val listqueuefree = ActorSystem("listqueuefree", configr)
//        listqueuefree.actorOf(ListQueueActor.props, "free")

        val config = ConfigFactory.load("split-master")
        val system = ActorSystem("calc", config)
        ActorSystem("queue").scheduler.schedule(1 seconds, 2 seconds , system.actorOf(QueueActor.props), ThreadQueue())
        val node_ip = system.settings.config.getStringList("akka.cluster.seed-nodes")
        if(system.settings.config.getStringList("akka.cluster.roles").contains("splitmaster")) {
            Cluster(system).registerOnMemberUp {
                system.actorOf(SplitReception.props, "splitreception")
            }
//            for(i <- 1 to (node_ip.toArray.tail.size)) {
//                val c = ConfigFactory.load("split-worker_"+i)
//                val s = ActorSystem("calc", c)
//                val a = s.actorOf(AkkaReception.props)
//                ListQueue.ListNode_Queue((0, a, node_ip.get(i)))
//            }

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
