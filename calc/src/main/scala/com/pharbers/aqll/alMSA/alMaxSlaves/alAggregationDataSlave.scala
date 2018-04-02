package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.pattern.ask

import scala.concurrent.duration._
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props, SupervisorStrategy}
import akka.util.Timeout
import com.pharbers.aqll.alCalcHelp.alLog.alTempLog
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.takeNodeForRole
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.aggregationMsg.{aggregationDataHand, aggregationDataImpl}

import scala.concurrent.Await

object alAggregationDataSlave {
	def props: Props = Props[alAggregationDataSlave]
	def name = "aggregation-data-slave"
}

class alAggregationDataSlave extends Actor with ActorLogging {
	override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
		case _ => Restart
	}
	
	def aggregation: Receive = {
		case aggregationDataHand() =>
			implicit val t: Timeout = Timeout(2 seconds)
			val a = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
			val f = a ? takeNodeForRole("splitaggregationslave")
			if (Await.result(f, t.duration).asInstanceOf[Boolean])
				sender ! aggregationDataHand()
			else Unit
			
		case aggregationDataImpl(uid, company, temp) =>
			val counter = context.actorOf(alCommonErrorCounter.props)
			val cur = context.actorOf(alAggregationDataComeo.props(uid, temp, counter))
			cur.tell(aggregationDataImpl(uid, company, temp), sender)
			
		case msg => alTempLog(s"Warning! Message not delivered. alAggregationDataSlave.received_msg=$msg")
	}
	
	override def receive: Receive = aggregation
}
