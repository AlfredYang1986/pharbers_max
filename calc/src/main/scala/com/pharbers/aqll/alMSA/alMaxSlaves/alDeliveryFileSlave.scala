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
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.generateDeliveryFile.{generateDeliveryFileHand, generateDeliveryFileImpl}

import scala.concurrent.Await

object alDeliveryFileSlave {
	def props: Props = Props[alDeliveryFileSlave]
	def name = "delivery-file-slave"
}

class alDeliveryFileSlave extends Actor with ActorLogging {
	override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
		case _ => Restart
	}
	
	def delivery: Receive = {
		case generateDeliveryFileHand() =>
			implicit val t: Timeout = Timeout(2 seconds)
			val a = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
			val f = a ? takeNodeForRole("splitdeliveryslave")
			if (Await.result(f, t.duration).asInstanceOf[Boolean])
				sender ! generateDeliveryFileHand()
			else Unit
			
		case generateDeliveryFileImpl(uid, company, temp) =>
			val counter = context.actorOf(alCommonErrorCounter.props)
			val cur = context.actorOf(alDeliveryFileComeo.props(uid, temp, counter))
			cur.tell(generateDeliveryFileImpl(uid, company, temp), sender)
			
		case msg => alTempLog(s"Warning! Message not delivered. alDeliveryFileSlave.received_msg=$msg")
	}
	
	override def receive: Receive = delivery
}
