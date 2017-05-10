package com.pharbers.aqll.old.calc.alcalc.alexception

import akka.actor.{Actor, Props}

/**
  * Created by qianpeng on 2017/4/11.
  */

object alExceptionMsg {
	def props = Props[alExceptionMsg]
}

class alExceptionMsg extends Actor{

	def exmsg: Receive = {
		case _ => ???
	}

	override def receive = exmsg
}
