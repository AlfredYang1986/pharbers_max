package test

import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import akka.actor.ActorSystem
import akka.util.Timeout
import akka.pattern.ask
import scala.concurrent.Await
import com.pharbers.aqll.calc.maxmessages.excelJobStart
import com.pharbers.aqll.calc.split.JobCategories.cpaProductJob

object MainTest2Call extends App{
	implicit val timeout = Timeout(60 second)
		val conf = """akka {
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
						      port = 4772
						    }
						  }
						}
	                  """
		val config = ConfigFactory.parseString(conf)
		val path = "akka.tcp://ExcelMain@127.0.0.1:4771/user/sample"
		val system = ActorSystem("forend",config)
		val sample = system.actorSelection(path)
		println(system)
		println(sample)
//		val aa = sample ? excelJobStart("""config/test/BMS客户上传/201601-07-CPA-Baraclude产品待上传.xlsx""", cpaProductJob, "098f6bcd4621d373cade4e832627b4f6", 0)
//		val bb = Await.result(aa.mapTo[String], timeout.duration)
//		if(bb.equals("is ok")) {
//			println(bb)
//			system.shutdown()
//		}

	//split-master akka/check-remote

//	val config = ConfigFactory.load("resources/akka/check-remote")
//	println(config)
}