package com.pharbers.aqll.pattern

import akka.actor.ActorRef
import akka.actor.Props

object ScatterGatherStepActor {
	def prop(sga : ActorRef, msr : MessageRoutes) : Props = {
		Props(new ScatterGatherStepActor(sga, msr))
	}
}

class ScatterGatherStepActor(sga : ActorRef, msr : MessageRoutes) extends PipeFilterActor(sga, msr) {
	override def dispatchImpl(cmd : CommonMessage, module : ModuleTrait) = {
		tmp = Some(true)
		module.dispatchMsg(cmd)(rst) match {
			case (_, Some(err)) => {
				sga ! ParalleMessageFailed(err)
				cancelActor					
			}
			case (Some(r), _) => {
				rst = Some(r) 
			}
			case _ => println("never go here")
		}
		rstReturn
	}
	
	override def rstReturn = tmp match {
		case Some(_) => { rst match {
			case Some(r) => 
				msr.lst match {
					case Nil => {
						sga ! ParalleMessageSuccess(r)
					}
					case head :: tail => {
						next = context.actorOf(ScatterGatherStepActor.prop(sga, MessageRoutes(tail, rst)), "step")
						next ! head
					}
					case _ => println("msr error")
				}
				cancelActor
			case _ => Unit
		}}
		case _ => println("never go here"); Unit
	}
}