package scala.com.pharbers.aqll.calc.Http

import scala.concurrent.Future

import akka.actor.{ ActorSystem , Actor, Props }
import akka.event.Logging
import akka.util.Timeout

import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import com.typesafe.config.{ Config, ConfigFactory }

/**
  * Created by Faiz on 2017/1/7.
  */
object CheckExcelHttpApp extends App with RequestTimeout {
    val config = ConfigFactory.load("application")
    val host = config.getString("http.host")
    val port = config.getInt("http.port")
    println(host)
    println(port)

    implicit val system = ActorSystem("CheckMain")
    implicit val ec = system.dispatcher

    val api = new OrderServiceApi(system, requestTimeout(config)).routes

    implicit val materializer = ActorMaterializer()
    val bindingFuture: Future[ServerBinding] = Http().bindAndHandle(api, host, port)

    bindingFuture.map { serverBinding =>
        println(s"serverBinding = ${serverBinding}")
    }.onFailure {
        case ex: Exception =>
            system.terminate()
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
