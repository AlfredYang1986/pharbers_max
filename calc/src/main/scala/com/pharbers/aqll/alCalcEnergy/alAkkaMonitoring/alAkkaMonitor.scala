package com.pharbers.aqll.alCalcEnergy.alAkkaMonitoring

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSelection, Address, Props, Terminated}
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, Member}
import com.pharbers.aqll.alCalcMemory.aljobs.alRegisterJob
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger.{calc_register, group_register}
import com.pharbers.aqll.alCalcMemory.alprecess.alprecessdefines.alPrecessDefines.do_register
import com.pharbers.aqll.alCalcMemory.alprecess.alsplitstrategy.server_info

import scala.concurrent.stm.atomic

/**
  * Created by qianpeng on 2017/6/1.
  */

object alAkkaMonitor {
	val props = Props[alAkkaMonitor]
	var groupRouter = Seq[ActorRef]()
	var calcRouter = Seq[ActorRef]()
}

class alAkkaMonitor extends Actor with ActorLogging with alRegisterJob{
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
		case MemberJoined(member) => println("Member Joined")
		case MemberUp(member) =>
			cur = Some(RegisterGroup(member.address, group_register(self)) :: RegisterCalc(member.address, calc_register(self)) :: Nil)
			process = do_register() :: Nil
			super.excute()
			atomic { implicit txn => server_info.section() = server_info.section() + 1 }
		
		case UnreachableMember(member) =>
			atomic { implicit txn => server_info.section() = server_info.section() - 1 }
			removedAcotr(member)
			
//		case MemberRemoved(member, previousStatus) => log.info("Member Remove")
		
		case group_register(act) =>
			groupRouter = groupRouter :+ act
			context.watch(act)
		
		case calc_register(act) =>
			calcRouter = calcRouter :+ act
			context.watch(act)
			
		case Terminated(a) =>
//			groupActor = groupActor.filterNot( _ == a)
//			calcActor = calcActor.filterNot( _ == a)
			groupRouter = groupRouter.filterNot{ x =>
				if(x == a) {
					cur = Some(RegisterGroup(a.path.address, group_register(self)) :: Nil)
					process = do_register() :: Nil
					super.excute()
					true
				} else false
			}

			calcRouter = calcRouter.filterNot{ x =>
				if(x == a) {
					cur = Some(RegisterCalc(a.path.address, calc_register(self)) :: Nil)
					process = do_register() :: Nil
					super.excute()
					true
				} else false
			}
		
		case x => println(x)
	}
	
	/**
	  * 针对于节点因为未知原因重启或者断网导致出错，来删除相应节点的Actor
	  */
	def removedAcotr(member: Member): Unit = {
		groupRouter = groupRouter.filterNot(x => x.path.address == member.address)
		calcRouter = calcRouter.filterNot(x => x.path.address == member.address)
	}
	
	case class RegisterGroup(address: Address, msg: Any) extends alRegisterCommond {
		override def actorSelection: ActorSelection = context.actorSelection(s"${address}/user/registergroup")
		override def message: Any = msg
	}
	
	case class RegisterCalc(address: Address, msg: Any) extends alRegisterCommond {
		override def actorSelection: ActorSelection = context.actorSelection(s"${address}/user/registercalc")
		override def message: Any = msg
	}
}

trait alRegisterCommond {
	def actorSelection: ActorSelection = null
	
	def message: Any
	
	def register(): Unit = actorSelection ! message
	
//	def actorSeq: Option[Seq[ActorRef]] = None
//
//	def member: Option[Member] = None
//
//	def removed(): Unit = None
}