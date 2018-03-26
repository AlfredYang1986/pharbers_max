package com.pharbers.aqll.alMSA.alMaxSlaves

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, PoisonPill, Props}
import com.pharbers.aqll.alCalcHelp.alLog.alTempLog
import com.pharbers.aqll.alCalcHelp.alWebSocket.phWebSocket
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.generateDeliveryFile.{generateDeliveryFileEnd, generateDeliveryFileImpl, generateDeliveryFileResult, generateDeliveryFileTimeOut}
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.reStartMsg.canIReStart

import scala.concurrent.duration._
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import com.pharbers.aqll.common.alFileHandler.databaseConfig.db1
import com.pharbers.aqll.common.alFileHandler.fileConfig.memorySplitFile
import com.pharbers.aqll.common.alFileHandler.fileConfig.export_file
import com.pharbers.delivery.nhwa.DriverNHWA

import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.immutable.Map

object alDeliveryFileComeo {
	def props(uid: String, listJob: List[String], counter: ActorRef): Props = Props(new alDeliveryFileComeo(uid, listJob, counter))
}

class alDeliveryFileComeo(uid: String, listJob: List[String], counter: ActorRef) extends Actor with ActorLogging {
	
	val timeoutMessager: Cancellable = context.system.scheduler.scheduleOnce(10 hours) {
		self ! generateDeliveryFileTimeOut()
	}
	
	override def postRestart(reason: Throwable): Unit = {
		counter ! canIReStart(reason)
	}
	
	def delivery: Receive = {
		case generateDeliveryFileImpl(_, company, listJob) => {
            try {
                alTempLog(s"Start generateDeliveryFileImpl")

                val driverNHWA = DriverNHWA()
                val listDF = listJob.map(temp => driverNHWA.generateDeliveryFileFromMongo(s"$db1", s"$company$temp"))
                val originFilePath = driverNHWA.save2File(driverNHWA.unionDataFrameList(listDF))
                val uuid = UUID.randomUUID().toString
                val fileName = s"${company}-${uuid}.csv"
                driverNHWA.move2ExportFolder(originFilePath, s"${memorySplitFile}${export_file}${fileName}")
                driverNHWA.closeSparkSession

                self ! generateDeliveryFileEnd(fileName, true)
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
