package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, PoisonPill, Props}
import com.pharbers.aqll.alCalcHelp.alFinalDataProcess.alWeightSum
import com.pharbers.aqll.alCalcHelp.alLog.alTempLog
import com.pharbers.aqll.alCalcHelp.alWebSocket.phWebSocket
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.aggregationMsg._
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.generateDeliveryFile.{generateDeliveryFileEnd, generateDeliveryFileImpl, generateDeliveryFileResult, generateDeliveryFileTimeOut}
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.reStartMsg.canIReStart

import scala.concurrent.duration._
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import com.pharbers.driver.redis.phRedisDriver
import com.pharbers.aqll.common.alFileHandler.databaseConfig.db1
import com.pharbers.nhwa.DriverNHWA

import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.immutable.Map

object alDeliveryFileComeo {
	def props(uid: String, table: String, counter: ActorRef): Props = Props(new alDeliveryFileComeo(uid, table, counter))
}

class alDeliveryFileComeo(uid: String, table: String, counter: ActorRef) extends Actor with ActorLogging {
	
	val timeoutMessager: Cancellable = context.system.scheduler.scheduleOnce(10 hours) {
		self ! generateDeliveryFileTimeOut()
	}
	
	override def postRestart(reason: Throwable): Unit = {
		counter ! canIReStart(reason)
	}
	
	def delivery: Receive = {
		case generateDeliveryFileImpl(_, company, temp) => {
            try {
                alTempLog(s"Start generateDeliveryFileImpl")
                val driverNHWA = DriverNHWA()
                val deliveryFile = driverNHWA.generateDeliveryFileFromMongo(s"$db1", s"$company$temp")
                self ! generateDeliveryFileEnd(deliveryFile, true)
            } catch {
                case ex: Exception => self ! generateDeliveryFileEnd(ex.getMessage, false)
            }
        }

		case generateDeliveryFileEnd(fileName, result) =>
			if (result) {
				alTempLog(s"delivery ${fileName} => Success")
			} else {
				val msg = Map(
					"type" -> "error",
					"error" -> s"cannot generate delivery file, exception is ${fileName}"
				)
				phWebSocket(uid).post(msg)
				alTempLog(s"delivery data => Failed because ${fileName}")
			}
			shutSlaveCameo(generateDeliveryFileResult(uid, fileName, result))
		
		case generateDeliveryFileTimeOut() =>
			alTempLog("delivery data => TimeOut")
			self ! generateDeliveryFileEnd("delivery data => TimeOut", false)
			
		case msg : AnyRef => log.info(s"Warning! Message not delivered. alDeliveryFileComeo.received_msg=$msg")
	}
	
	override def receive: Receive = delivery
	
	def shutSlaveCameo(msg: AnyRef): Unit = {
		timeoutMessager.cancel()
		context.actorSelection("akka.tcp://calc@" + masterIP + ":2551/user/agent-reception") ! msg
		alTempLog("stop delivery data cameo")
		self ! PoisonPill
	}
}
