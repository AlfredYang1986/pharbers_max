package com.pharbers.aqll.alCalcEnergy

import akka.actor.{Actor, ActorLogging, Props}
import com.pharbers.aqll.alCalc.almain.alGroupActor
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger.group_register

/**
  * Created by qianpeng on 2017/6/5.
  */

object alGroupRegisterActor {
	def props = Props[alGroupRegisterActor]
}

class alGroupRegisterActor extends Actor with ActorLogging with alSupervisorStrategy {
	val register: Receive = {
		case group_register(act) =>
			act ! group_register(context.actorOf(alGroupActor.props))
		case _ => ???
	}
	
	override def receive: Receive = register
}
