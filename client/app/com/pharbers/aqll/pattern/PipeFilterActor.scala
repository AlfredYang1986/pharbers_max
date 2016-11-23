package com.pharbers.aqll.pattern

import scala.concurrent.duration._

import akka.actor.Actor
import akka.actor.ActorContext
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import play.api.Application
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

import module.ExampleModuleMessage._
import module.ExampleModule

object PipeFilterActor {
	def prop(originSender : ActorRef, msr : MessageRoutes) : Props = {
		Props(new PipeFilterActor(originSender, msr))
	}
}

class PipeFilterActor(originSender : ActorRef, msr : MessageRoutes) extends Actor with ActorLogging {
	
	def dispatchImpl(cmd : CommonMessage, module : ModuleTrait) = {
		tmp = Some(true)
		module.dispatchMsg(cmd)(rst) match {
			case (_, Some(err)) => {
				originSender ! error(err)
				cancelActor					
			}
			case (Some(r), _) => {
				rst = Some(r) 
			}
			case _ => println("never go here")
		}
		rstReturn
	}
	
	var tmp : Option[Boolean] = None
	var rst : Option[Map[String, JsValue]] = msr.rst
	var next : ActorRef = null
	def receive = {
		case cmd : msg_exampleBase => dispatchImpl(cmd, ExampleModule)
		case cmd : msg_ResultCommand => dispatchImpl(cmd, ResultModule)
		case timeout() => {
			originSender ! new timeout
			cancelActor
		}
	 	case _ => ???
	}
	
	val timeOutSchdule = context.system.scheduler.scheduleOnce(2 second, self, new timeout)

	def rstReturn = tmp match {
		case Some(_) => { rst match {
			case Some(r) => 
				msr.lst match {
					case Nil => {
						originSender ! result(toJson(r))
					}
					case head :: tail => {
						next = context.actorOf(PipeFilterActor.prop(originSender, MessageRoutes(tail, rst)), "pipe")
						next ! head
					}
					case _ => println("msr error")
				}
				cancelActor
			case _ => Unit
		}}
		case _ => println("never go here"); Unit
	}
	
	def cancelActor = {
		timeOutSchdule.cancel
//		context.stop(self)
	}
}