package com.pharbers.aqll.pattern

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorLogging
import play.api.libs.json.Json.toJson
import akka.actor.Terminated

class RoutesActor extends Actor with ActorLogging {
	var originSender : ActorRef = null
	var next : ActorRef = null
	def receive = {
		case excute(msr)  => {
			implicit val db = msr.db
			originSender = sender
			msr.lst match {
				case Nil => originSender ! toJson("error")
				case head :: tail => {
					head match {
						case p : ParallelMessage => {
							next = context.actorOf(ScatterGatherActor.prop(self, MessageRoutes(tail, msr.rst)), "gate")
							next ! head
						}
						case c : CommonMessage => {
							next = context.actorOf(PipeFilterActor.prop(self, MessageRoutes(tail, msr.rst)), "gate")
							next ! head
						}
					}
					
					context.watch(next)
				}
			}
		}
		case result(rst) => {
			originSender ! rst
			cancelActor
		}
		case error(err) => {
			originSender ! err
			cancelActor
		}
		case timeout() => {
			originSender ! toJson("timeout")
			cancelActor
		}
		case Terminated(actorRef) => println("Actor {} terminated", actorRef)
		case _ => Unit
 	}
	
	def cancelActor = {
		context.stop(self)
	}
}