package com.pharbers.aqll.calc.split

import akka.actor.{Actor, ActorLogging, Props}

/**
  * Created by qianpeng on 2017/3/9.
  */
object SplitMarket {
	def props = Props[SplitMarket]
}

class SplitMarket extends Actor with ActorLogging{
	def market: Receive = {


		case _ => ???
	}

	override def receive = market
}
