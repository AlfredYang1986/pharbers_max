package com.pharbers.aqll.calc.Http

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.pharbers.aqll.calc.util.{GetProperties, ScpCopyFile}

/**
  * Created by faiz on 17-2-6.
  */
case class SCPServerInfo(server: String, map: Map[String, String], actorRef: ActorRef, anyRef: AnyRef)
object ScpCopyActor {
	def props = Props[ScpCopyActor]
}

class ScpCopyActor extends Actor with ActorLogging{
	val scp: Receive = {
		case SCPServerInfo(server, map, actorRef, anyRef) => {
			val user = GetProperties.loadConf("File.conf").getString("SCP.Server.user")
			val pass = GetProperties.loadConf("File.conf").getString("SCP.Server.pass")
			ScpCopyFile(server, user, pass, map) match {
				case false => println("SCP Copy File Exception")
				case _ => {
					println(s"actorRef = $actorRef")
					println(s"anyRef = $anyRef")
					actorRef ! anyRef
					context.stop(self)
				}
			}
		}
	}
	def receive = scp
}
