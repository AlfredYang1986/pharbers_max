package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, PoisonPill, Props}
import akka.pattern.ask
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.routing.BroadcastPool
import akka.util.Timeout
import com.pharbers.aqll.alCalcHelp.alLog.alTempLog
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.queryIdleNodeInstanceInSystemWithRole
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.aggregationMsg._
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.generateDeliveryFile.{generateDeliveryFileHand, generateDeliveryFileImpl, generateDeliveryFileSchedule, generateDeliveryFileStart}
import com.pharbers.aqll.alMSA.alMaxSlaves.{alAggregationDataSlave, alDeliveryFileSlave}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.stm.{Ref, atomic}
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import com.pharbers.driver.redis.phRedisDriver

import scala.concurrent.ExecutionContext.Implicits.global

trait alGenerateDeliveryFIleTrait { this: Actor =>
	
	val delivery_router: ActorRef = createDeliveryRouter
	val delivery_jobs = Ref(List[(String, List[String])]())

	val deliverySchedule: Cancellable = context.system.scheduler.schedule(5 seconds, 1 seconds, self, generateDeliveryFileSchedule())

	def createDeliveryRouter: ActorRef = context.actorOf(
		ClusterRouterPool(BroadcastPool(1),
			ClusterRouterPoolSettings(
				totalInstances = 1,
				maxInstancesPerNode = 1,
				allowLocalRoutees = false,
				useRole = Some("splitdeliveryslave")
			)
		).props(alDeliveryFileSlave.props), name = "delivery-file-router")

	def pushDeliveryJobs(uid: String, listJob: List[String]): Unit = {
		atomic { implicit thx =>
			delivery_jobs() = delivery_jobs() :+ uid -> listJob
		}
	}

	def canDoDeliveryJob : Boolean = {
		implicit val t: Timeout = Timeout(2 seconds)
		val a = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
		val f = a ? queryIdleNodeInstanceInSystemWithRole("splitdeliveryslave")
		Await.result(f, t.duration).asInstanceOf[Int] > 0
	}

	def deliveryScheduleJobs(): Unit = {
		if (canDoDeliveryJob) {
			atomic { implicit thx =>
				val tmp = delivery_jobs.single.get
				if (tmp.isEmpty) Unit
				else {
					delivery_jobs() = delivery_jobs().tail
					generateDeliveryFile(tmp.head._1, tmp.head._2)
				}
			}
		}
	}

	def generateDeliveryFile(uid: String, listJob: List[String]): Unit = {
		val cur = context.actorOf(alCameoDeliveryFile.props(uid, listJob, delivery_router))
		cur ! generateDeliveryFileStart()
	}
}

object alCameoDeliveryFile {
	def props(uid: String, listJob: List[String], slaveActor: ActorRef): Props = Props(new alCameoDeliveryFile(uid, listJob, slaveActor))
}

class alCameoDeliveryFile(uid: String, listJob: List[String], slaveActor: ActorRef) extends Actor with ActorLogging {
	def delivery: Receive = {
		case generateDeliveryFileStart() => slaveActor ! generateDeliveryFileHand()
		case generateDeliveryFileHand() =>
			val company = phRedisDriver().commonDriver.hget(uid, "company").map(x=>x).getOrElse(throw new Exception("not found company"))
			sender() ! generateDeliveryFileImpl(uid, company, listJob)
			shutCameo()
		case msg =>
			alTempLog(s"Warning! Message not delivered. alCameoDeliveryFile.received_msg=$msg")
			shutCameo()
	}
	
	override def receive: Receive = delivery
	
	def shutCameo(): Unit = {
		alTempLog("stopping delivery cameo")
		self ! PoisonPill
	}
}
