package com.pharbers.aqll.alMSA.alMaxCmdMessage

import com.pharbers.aqll.common.alCmd.scpcmd.scpCmd
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.pharbers.aqll.alMSA.alMaxCmdMessage.alCmdActor._
import com.pharbers.aqll.common.alCmd.pkgcmd.{pkgCmd, unPkgCmd}

object alCmdActor {
	def props() = Props[alCmdActor]

	case class pkgmsg(file: List[String], target: String)
	case class scpmsg(file: String, target: String, host: String, user: String)
	case class unpkgmsg(target: String, des_dir: String, s: ActorRef)

	sealed class stop(t: Int, n: String)
	case class scpend(s: ActorRef) extends stop(0, "scp")
	case class pkgend(s: ActorRef) extends stop(1, "pkg")
	case class unpkgend(s: ActorRef) extends stop(2, "unpkg")
}

class alCmdActor extends Actor with ActorLogging {
	override def receive: Receive = {
		case pkgmsg(file, target) => {
			pkgCmd(file, target).excute
			sender() ! pkgend(self)
		}
		case scpmsg(file, target, host, user) => {
			scpCmd(file, target, host, user).excute
			sender() ! scpend(self)
		}
		case unpkgmsg(target, des_dir, s) => {
			unPkgCmd(target, des_dir).excute
			sender() ! unpkgend(s)
		}
		case _ => ???
	}
}
