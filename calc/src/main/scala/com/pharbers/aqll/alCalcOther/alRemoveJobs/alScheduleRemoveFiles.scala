package com.pharbers.aqll.alCalcOther.alRemoveJobs

import java.util.Calendar

import akka.actor.{Actor, ActorLogging, Props}
import com.pharbers.aqll.common.alFileHandler.fileConfig._
import com.pharbers.aqll.common.alFileHandler.timingConfig._
import com.pharbers.aqll.common.alFileHandler.alFilesOpt.alFileOpt

/**
  * Created by qianpeng on 2017/3/27.
  */

trait alFilePath {

	def calcPath = root + program + memorySplitFile

	def path = root + program

	def convertStr(str: String) = str.substring(0, str.lastIndexOf("/"))
}

case class rmFile()

object alScheduleRemoveFiles extends alFilePath{

	def props = Props[alScheduleRemoveFiles]

	val rmLst = calcPath + calc ::
				calcPath + fileTarGz ::
				calcPath + group ::
				calcPath + sync ::
				path + scpPath ::
				path + dumpdb :: Nil
}

class alScheduleRemoveFiles extends Actor with ActorLogging{

	// TODO : 要删除的目录 SCP、sync、group、dbdump
	val remove: Receive = {
		case rmFile() =>
			val time = Calendar.getInstance
			if (hours == time.get(Calendar.HOUR_OF_DAY) &&
				minutes == time.get(Calendar.MINUTE) &&
				seconds.toInt > time.get(Calendar.SECOND)) {
				alScheduleRemoveFiles.rmLst foreach { x =>
					val flag = alFileOpt(x).removeCurFiles
					if(!flag) println() else println()
				}
			}
		case _ => ???
	}

	override def receive = remove
}
