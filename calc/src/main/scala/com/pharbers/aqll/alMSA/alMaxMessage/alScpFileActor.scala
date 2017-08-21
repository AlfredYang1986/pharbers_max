package com.pharbers.aqll.alMSA.alMaxMessage

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.pharbers.aqll.common.alCmd.pkgcmd.{pkgCmd, unPkgCmd}
import com.pharbers.aqll.common.alCmd.scpcmd.scpCmd

object alFileActor {
	def props() = Props[alFileActor]
}

case class scpmsg(file: String, target: String, host: String, user: String)
case class pkgmsg(file: List[String], target: String)
case class unpkgmsg(target: String, des_dir: String)

sealed class stop(t: Int, n: String)
case class scpend(s: ActorRef) extends stop(0, "scp")
case class pkgend(s: ActorRef) extends stop(1, "pkg")
case class unpkgend(s: ActorRef) extends stop(2, "unpkg")

class alFileActor extends Actor with ActorLogging {
	
	def msg: Receive = {
		case scpmsg(file, target, host, user) => {
			scpCmd(file, target, host, user).excute
			sender() ! scpend(self)
		}
		case pkgmsg(file, target) => {
			pkgCmd(file, target).excute
			sender() ! pkgend(self)
		}
		case unpkgmsg(target, des_dir) => {
			unPkgCmd(target, des_dir).excute
			sender() ! unpkgend(self)
		}
		case _ => ???
	}
	
	override def receive: Receive = msg
}
