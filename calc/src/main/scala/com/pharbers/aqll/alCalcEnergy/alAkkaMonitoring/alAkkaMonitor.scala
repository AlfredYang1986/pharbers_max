package com.pharbers.aqll.alCalcEnergy.alAkkaMonitoring

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, Member}

/**
  * Created by qianpeng on 2017/6/1.
  */

object alAkkaMonitor {
	val props = Props[alAkkaMonitor]
}

class alAkkaMonitor extends Actor with ActorLogging {
	
	Cluster(context.system).subscribe(self, classOf[MemberEvent])
	val cluster = Cluster(context.system)
	var memberSeq = Seq[Member]()
	var actorSeq = Seq[ActorRef]()
	
	override def preStart() = {
		cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
			classOf[MemberUp], classOf[MemberExited], classOf[MemberRemoved], classOf[UnreachableMember], classOf[MemberEvent])
	}
	override def postStop() = {
		cluster.unsubscribe(self)
	}
	
	override def receive = {
		case MemberJoined(member) => log.info("Member Join")
		case MemberUp(member) => log.info("Member Up")
		case MemberExited(member) => log.info("Member Exited")
		case MemberRemoved(member, previousStatus) => log.info("Member Remove")
	}
	
	def register(member: Member) = {
		println(member.address)
		memberSeq = memberSeq :+ member
	}
}
