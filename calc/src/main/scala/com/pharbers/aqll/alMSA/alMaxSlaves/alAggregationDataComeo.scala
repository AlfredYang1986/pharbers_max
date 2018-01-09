package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, PoisonPill, Props}
import com.pharbers.aqll.alCalcHelp.alFinalDataProcess.alWeightSum
import com.pharbers.aqll.alCalcHelp.alLog.alTempLog
import com.pharbers.panel.util.phWebSocket
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.aggregationMsg._
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.reStartMsg.canIReStart

import scala.concurrent.duration._
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import com.pharbers.driver.redis.phRedisDriver
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.immutable.Map

object alAggregationDataComeo {
	def props(uid: String, table: String, counter: ActorRef): Props = Props(new alAggregationDataComeo(uid, table, counter))
}

class alAggregationDataComeo(uid: String, table: String, counter: ActorRef) extends Actor with ActorLogging {
	
	val timeoutMessager: Cancellable = context.system.scheduler.scheduleOnce(10 hours) {
		self ! aggregationDataTimeOut()
	}
	
	override def postRestart(reason: Throwable): Unit = {
		counter ! canIReStart(reason)
	}
	
	def aggregation: Receive = {
		case aggregationDataImpl(uid, company, temp) =>
			alWeightSum(uid, company, s"$company$temp", 1).aggregation
			self ! aggregationDataEnd(true)
			
		case aggregationDataEnd(result) =>
			if (result) {
				alTempLog("aggregation data => Success")
			} else {
				val msg = Map(
					"type" -> "error",
					"error" -> "cannot aggregation data"
				)
				phWebSocket(uid).post(msg)
				alTempLog("aggregation data => Failed")
			}
			shutSlaveCameo(aggregationDataResult(uid, table, result))
		
		case aggregationDataTimeOut() =>
			alTempLog("aggregation data => TimeOut")
			self ! aggregationDataEnd(false)
			
		case msg : AnyRef => log.info(s"Warning! Message not delivered. alAggredationCameo.received_msg=$msg")
	}
	
	override def receive: Receive = aggregation
	
	def shutSlaveCameo(msg: AnyRef): Unit = {
		timeoutMessager.cancel()
		context.actorSelection("akka.tcp://calc@" + masterIP + ":2551/user/agent-reception") ! msg
		alTempLog("stop aggregation data cameo")
		self ! PoisonPill
	}
}
