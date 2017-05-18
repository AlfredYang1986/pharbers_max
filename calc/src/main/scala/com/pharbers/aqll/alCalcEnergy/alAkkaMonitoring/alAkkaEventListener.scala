package com.pharbers.aqll.alCalcEnergy.alAkkaMonitoring

import akka.actor.{Actor, ActorLogging}
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, Member}

/**
  * Created by qianpeng on 2017/4/11.
  */
class alAkkaEventListener extends Actor with ActorLogging{
	Cluster(context.system).subscribe(self, classOf[MemberEvent])
	val cluster = Cluster(context.system)
	var members = Seq[Member]()

	override def preStart(): Unit = {
		cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
			classOf[MemberUp], classOf[UnreachableMember], classOf[MemberEvent])
	}
	override def postStop(): Unit = {
		cluster.unsubscribe(self)
	}



	override def receive ={
		case MemberJoined(member) =>
			println("Member Join")
		case MemberUp(member) =>
			println("Member Up")
		case MemberExited(member) =>
			println("Member Exited")
		case MemberRemoved(member, previousStatus) =>
			println("Member Removed")
		case x: MemberEvent =>
			println(s"MemberEvent: ${x}")

	}
}
