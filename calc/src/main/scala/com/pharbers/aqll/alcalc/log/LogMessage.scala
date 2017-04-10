package com.pharbers.aqll.alcalc.log

/**
  * Created by qianpeng on 2017/4/10.
  */
import org.apache.log4j.{Logger, PropertyConfigurator}
import akka.actor.ActorRef
trait log {
	def loadProperties = {
		try {
			PropertyConfigurator.configure("config/log/log4j.properties")
		}catch {
			case ex : Exception => println(s"can not properties Exception: $ex")
		}
	}
}

object LogMessage extends log{
	loadProperties
	def info(senderR : ActorRef, classStr: String, args : String = "") {
		val logger_info = Logger.getLogger("MaxInfo")
		logger_info.info(s"Sender: ${senderR.path} Class: ${classStr} Args: ${args}")
	}

	def error(senderR : ActorRef, classStr: String, args : String = "") = {
		val logger_error = Logger.getLogger("MaxError")
		logger_error.error(s"Sender: ${senderR.path} Class: ${classStr} Args: ${args}")
	}
}