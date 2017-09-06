package com.pharbers.aqll.alCalcEnergy

import akka.actor.{Actor, ActorLogging, Props}
import com.pharbers.aqll.alCalc.almain.alCalcActor
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger.calc_register

/**
  * Created by qianpeng on 2017/6/5.
  */

object alCalcRegisterActor {
	def props = Props[alCalcRegisterActor]
}

class alCalcRegisterActor extends Actor with ActorLogging{
	val register: Receive = {
		case calc_register(act) =>
			act ! calc_register(context.actorOf(alCalcActor.props))
		case _ => ???
	}
	
	override def receive: Receive = register
}
