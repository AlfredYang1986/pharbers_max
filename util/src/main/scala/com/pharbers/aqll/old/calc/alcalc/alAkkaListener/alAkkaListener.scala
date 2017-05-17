package com.pharbers.aqll.old.calc.alcalc.alAkkaListener

import akka.actor.{Props, Terminated}
import akka.cluster.ClusterEvent._

/**
  * Created by qianpeng on 2017/4/11.
  */

object alAkkaListener {
	def props = Props(new alAkkaListener)
}

class alAkkaListener extends alAkkaEventListener{
	override def receive = {
		case MemberUp(member) =>
			println("====Member is Up: {}", member.address)
		//register(member, getCollectorPath)
		case MemberExited(member) =>
			println("....Member is Exited: {}",member.address)
		case UnreachableMember(member) =>
			println("----Member detected as Unreachable: {}", member)
		case MemberRemoved(member, previousStatus) =>
			println("++++Member is Removed: {} after {}", member.address, previousStatus)
		case Terminated(a) =>
			println(s"Terminated = $a")
	}
}
