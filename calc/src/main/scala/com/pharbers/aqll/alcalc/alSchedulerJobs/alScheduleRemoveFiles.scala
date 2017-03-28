package com.pharbers.aqll.alcalc.alSchedulerJobs

import akka.actor.{Actor, Props}

/**
  * Created by qianpeng on 2017/3/27.
  */

case class rmFile(uuid: String)

object alScheduleRemoveFiles {
	def props = Props[alScheduleRemoveFiles]
}

class alScheduleRemoveFiles extends Actor{

	// TODO : 要删除的目录 SCP、sync、group、dbdump
	val remove: Receive = {
		case rmFile(uuid) =>
			println(uuid)

		case _ => ???
	}

	override def receive = remove
}
