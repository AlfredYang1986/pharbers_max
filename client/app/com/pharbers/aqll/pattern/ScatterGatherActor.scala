package com.pharbers.aqll.pattern

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import scala.concurrent.stm._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import ParallelMessage.f
import scala.concurrent.duration._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

object ScatterGatherActor {
	def prop(originSender : ActorRef, msr : MessageRoutes) : Props = {
		Props(new ScatterGatherActor(originSender, msr))
	}
}

class ScatterGatherActor(originSender : ActorRef, msr : MessageRoutes)(implicit f : List[Map[String, JsValue]] => Map[String, JsValue]) extends Actor with ActorLogging {
	implicit val db = msr.db
	var next : ActorRef = null
	var sub_act = Seq[ActorRef]()
	var excepted = 0
	val tmp_result : Ref[List[Map[String, JsValue]]] = Ref(List.empty)
	
	def receive = {
		case msg : ParallelMessage => {
			excepted = msg.msgs.length
			msg.msgs.foreach { m => 
				val act = context.actorOf(ScatterGatherStepActor.prop(self, MessageRoutes(m.lst.tail, None)))
				act ! m.lst.head
				sub_act = sub_act :+ act
			}
		}
		case ParalleMessageSuccess(r) => {
			atomic { implicit thx => 
				tmp_result() = tmp_result() :+ r 
			}
		
			if (tmp_result.single.get.length == excepted) 
				rstReturn
		}
		case ParalleMessageFailed(err) => {
			originSender ! error(err)
			cancelActor					
		}
		case _ => println("something messages"); ???
	}
	
	val timeOutSchdule = context.system.scheduler.scheduleOnce(2 second, self, new timeout)

	def rstReturn = {
		msr.lst match {
			case Nil => {
				originSender ! result(toJson(f(tmp_result.single.get)))
			}
			case head :: tail => {
				val rst = Some(f(tmp_result.single.get))
				head match {
					case p : ParallelMessage => {
						next = context.actorOf(ScatterGatherActor.prop(originSender, MessageRoutes(tail, rst)), "scat")
						next ! head
					}
					case c : CommonMessage => {
						next = context.actorOf(PipeFilterActor.prop(originSender, MessageRoutes(tail, rst)), "pipe")
						next ! head
					}
				}
			}
			case _ => println("msr error")
		}
		cancelActor
	}
	
	def cancelActor = {
		timeOutSchdule.cancel
//		context.stop(self)
	}
}