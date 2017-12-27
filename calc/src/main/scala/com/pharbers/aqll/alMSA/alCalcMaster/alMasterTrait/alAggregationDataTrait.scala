package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, PoisonPill, Props}
import akka.pattern.ask
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.routing.BroadcastPool
import akka.util.Timeout
import com.pharbers.aqll.alCalcHelp.alLog.alTempLog
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.queryIdleNodeInstanceInSystemWithRole
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.aggregationMsg._
import com.pharbers.aqll.alMSA.alMaxSlaves.alAggregationDataSlave

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.stm.{Ref, atomic}
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import scala.concurrent.ExecutionContext.Implicits.global

trait alAggregationDataTrait { this: Actor =>
	
	val aggregator_router: ActorRef = createAggregationRouter
	val aggregator_jobs = Ref(List[(String, String)]())
	
	val aggregatorSchdule: Cancellable = context.system.scheduler.schedule(5 seconds, 1 seconds, self, aggregationDataSchedule())
	
	def createAggregationRouter: ActorRef = context.actorOf(
		ClusterRouterPool(BroadcastPool(1),
			ClusterRouterPoolSettings(
				totalInstances = 1,
				maxInstancesPerNode = 1,
				allowLocalRoutees = false,
				useRole = Some("splitaggregationslave")
			)
		).props(alAggregationDataSlave.props), name = "aggregation-data-router")
	
	def pushAggregationJobs(uid: String, table: String): Unit = {
		atomic { implicit thx =>
			aggregator_jobs() = aggregator_jobs() :+ uid -> table
		}
	}
	
	def canAggregationJob : Boolean = {
		implicit val t: Timeout = Timeout(2 seconds)
		val a = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
		val f = a ? queryIdleNodeInstanceInSystemWithRole("splitaggregationslave")
		Await.result(f, t.duration).asInstanceOf[Int] > 0
	}
	
	def aggregationSchduleJobs(): Unit = {
		if (canAggregationJob) {
			atomic { implicit thx =>
				val tmp = aggregator_jobs.single.get
				if (tmp.isEmpty) Unit
				else {
					aggregator_jobs() = aggregator_jobs().tail
					aggregationData(tmp.head._1, tmp.head._2)
				}
			}
		}
	}
	
	def aggregationData(uid: String, table: String): Unit = {
		val cur = context.actorOf(alCameoAggregationData.props(uid, table, aggregator_router))
		cur ! aggregationDataStart()
	}
}

object alCameoAggregationData {
	def props(uid: String, table: String, slaveActor: ActorRef): Props = Props(new alCameoAggregationData(uid, table, slaveActor))
}

class alCameoAggregationData(uid: String, table: String, slaveActor: ActorRef) extends Actor with ActorLogging {
	def aggregation: Receive = {
		case aggregationDataStart() => slaveActor ! aggregationDataHand()
		case aggregationDataHand() =>
			sender() ! aggregationDataImpl(uid, table)
			shutCameo()
		case msg =>
			alTempLog(s"Warning! Message not delivered. alCameoAggregationData.received_msg=$msg")
			shutCameo()
	}
	
	override def receive: Receive = aggregation
	
	def shutCameo(): Unit = {
		alTempLog("stopping aggregation data cameo")
		self ! PoisonPill
	}
}
