package test

import akka.actor.SupervisorStrategy.{Restart, Stop}
import akka.actor.{Actor, ActorInitializationException, ActorKilledException, ActorLogging, ActorSystem, AllForOneStrategy, DeathPactException, Kill, PoisonPill, Props, SupervisorStrategy}
import akka.dispatch.Mailboxes
import akka.util.Timeout
import com.pharbers.aqll.alCalcMemory.alexception.alException
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

/**
  * Created by qianpeng on 2017/5/18.
  */

object Test1 extends App {
	import scala.concurrent.duration._
	import scala.concurrent.ExecutionContext.Implicits.global
	val timeout = Timeout(10 seconds)
	val system = ActorSystem("TestRestart")
	val act = system.actorOf(Props[ActorSupervisor])
	
	system.scheduler.scheduleOnce(timeout.duration, act, restart("fuck"))
}

trait Supervisor { this: Actor =>
	override val supervisorStrategy =
		AllForOneStrategy() {
			case _: ActorInitializationException ⇒ println("ActorInitializationException");Stop
			case _: ActorKilledException         ⇒ Stop
			case _: DeathPactException           ⇒ println("DeathPactException");Restart
			case _: Exception                    ⇒ println("Exception");Restart
			case _: Error                        => println("Error");Restart
		}
}

case class restart(str: String)
case class tt(str: String)

class ActorSupervisor extends Actor with Supervisor {
	val act = context.actorOf(Props[ActorTest])
	override def receive: Receive = {
		case restart(s) => act ! restart(s)
		case tt(s) =>
			println(s"String = $s")
		case _ => ???
	}
}

class ActorTest extends Actor with ActorLogging {
	override def receive: Receive = {
		case restart(s) =>
			println("==Restart==")
//			context stop self
			self ! Kill
			self ! "test2"
		case "test" => println("hello world")
		case "test2" => println("fuck")
		case _ => ???
	}
	
	override def postStop(): Unit = {
		println("停止")
		println(self)
	}
	override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
		println("重启中...")
		println(s"$message")
		self ! "test"
	}
}