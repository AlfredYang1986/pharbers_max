package com.pharbers.aqll.alMSA.alMaxMessage

import akka.actor.{Actor, ActorLogging, Props}
import com.pharbers.aqll.common.alCmd.scpcmd.scpCmd

object alScpFileActor {
	def props() = Props(new alScpFileActor())
}

case class scpmsg(file: String, target: String, host: String, user: String)
case class scpend()

class alScpFileActor extends Actor with ActorLogging {
	
	def msg: Receive = {
		case scpmsg(file, target, host, user) => {
			scpCmd(file,target, host, user).excute
			sender() ! scpend()
		}
		case _ => ???
	}
	
	override def receive: Receive = msg
}
