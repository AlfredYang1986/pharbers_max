package bmpattern

import scala.concurrent.duration._
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import bmmessages._

object PipeFilterActor {
	def prop(originSender : ActorRef, msr : MessageRoutes) : Props = {
		Props(new PipeFilterActor(originSender, msr))
	}
}

class PipeFilterActor(originSender : ActorRef, msr : MessageRoutes) extends Actor with ActorLogging {
    implicit val cm = msr.cm
	
	def dispatchImpl(cmd : CommonMessage, module : ModuleTrait) = {
		tmp = Some(true)
		module.dispatchMsg(cmd)(rst) match {
			case (_, Some(err)) => {
				originSender ! error(err)
				cancelActor
			}
			case (Some(r), _) => {
//				println(r)
				rst = Some(r)
				rstReturn
				cancelActor
			}
			case _ => println("never go here")
		}
	}
	
	var tmp : Option[Boolean] = None
	var rst : Option[Map[String, JsValue]] = msr.rst
	var next : ActorRef = null
	def receive = {
		case cmd : msg_ResultCommand => dispatchImpl(cmd, ResultModule)
        case cmd : msg_LogCommand => dispatchImpl(cmd, LogModule)
		case cmd : ParallelMessage => {
		    cancelActor
			next = context.actorOf(ScatterGatherActor.prop(originSender, msr), "scat")
			next ! cmd
		}
		case timeout() => {
			originSender ! new timeout
			cancelActor
		}
	 	case x : AnyRef => println(x); ???
	}
	
	val timeOutSchdule = context.system.scheduler.scheduleOnce(2000 second, self, new timeout)

	def rstReturn : Unit = tmp match {
		case Some(_) => { rst match {
			case Some(r) => 
				msr.lst match {
					case Nil => {
						originSender ! result(toJson(r))
					}
					case head :: tail => {
						head match {
							case p : ParallelMessage => {
								next = context.actorOf(ScatterGatherActor.prop(originSender, MessageRoutes(tail, rst)), "scat")
								next ! p
							}
							case c : CommonMessage => {
								next = context.actorOf(PipeFilterActor.prop(originSender, MessageRoutes(tail, rst)), "pipe")
								next ! c
							}
						}
					}
					case _ => println("msr error")
				}
			case _ => Unit
		}}
		case _ => println("never go here"); Unit
	}
	
	def cancelActor = {
		timeOutSchdule.cancel
//		context.stop(self) 		// 因为后创建的是前创建的子Actor，当父Actor stop的时候，子Actor 也同时Stop，不能进行传递了
	}
}