package com.pharbers.aqll.alCalcEnergy

import akka.actor.SupervisorStrategy.{Escalate, Restart, Stop}
import akka.actor.{Actor, ActorInitializationException, ActorKilledException, AllForOneStrategy, DeathPactException, OneForOneStrategy}

/**
  * Created by qianpeng on 2017/4/25.
  */
trait alCalcSupervisorStrategy { this: Actor =>
	override val supervisorStrategy =
		AllForOneStrategy() {
			case _: Exception => Stop
			case _: Error => Stop
		}
}

trait alSupervisorStrategy { this: Actor =>
	override val supervisorStrategy =
		AllForOneStrategy() {
			case _: ActorInitializationException => println("ActorInitializationException");Restart
			case _: ActorKilledException         => println("ActorKilledException");Restart
			case _: DeathPactException           => println("DeathPactException");Restart
			case _: Exception                    => println("Exception");Escalate
			case _: Error                        => println("Error");Stop
		}
}
