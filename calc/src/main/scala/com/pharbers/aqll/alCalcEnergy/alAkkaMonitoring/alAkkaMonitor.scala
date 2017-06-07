package com.pharbers.aqll.alCalcEnergy.alAkkaMonitoring

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, Member}
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger.{calc_register, group_register}

/**
  * Created by qianpeng on 2017/6/1.
  */

object alAkkaMonitor {
	val props = Props[alAkkaMonitor]
	var groupActor = Seq[ActorRef]()
	var calcActor = Seq[ActorRef]()
}

class alAkkaMonitor extends Actor with ActorLogging {
	import alAkkaMonitor._
	
	Cluster(context.system).subscribe(self, classOf[MemberEvent])
	val cluster = Cluster(context.system)
//	var memberSeq = Seq[Member]()
	
	
	
	override def preStart() = {
		cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
			classOf[MemberUp], classOf[MemberExited], classOf[MemberRemoved], classOf[UnreachableMember], classOf[MemberEvent])
	}
	override def postStop() = {
		cluster.unsubscribe(self)
	}
	
	override def receive = {
		case MemberJoined(member) =>
			println("Member Joined")
		case MemberUp(member) =>
			println(s"MemberUp Address = ${member.address}")
			register(member)
		
		case MemberExited(member) => log.info("Member Exited")
		case MemberRemoved(member, previousStatus) => log.info("Member Remove")
		
		case group_register(act) =>
			groupActor = groupActor :+ act
		case calc_register(act) =>
			calcActor = calcActor :+ act
			
		case Terminated(a) =>
			groupActor = groupActor.filterNot(_ == a)
			calcActor = calcActor.filterNot(_ == a)
		case x => println(x)
	}
	
	def register(member: Member) = {
		context.actorSelection(s"${member.address}/user/registergroup") ! group_register(self)
		context.actorSelection(s"${member.address}/user/registercalc") ! calc_register(self)
	}
}
