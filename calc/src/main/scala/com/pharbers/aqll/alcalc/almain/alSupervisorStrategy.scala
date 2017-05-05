package com.pharbers.aqll.alcalc.almain

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{Actor, AllForOneStrategy}

import scala.concurrent.duration._

/**
  * Created by qianpeng on 2017/4/25.
  */
trait alSupervisorStrategy { this: Actor =>
	override val supervisorStrategy =
		AllForOneStrategy() {
			case _: Exception => Stop
			case _: Error => Stop
		}
}
