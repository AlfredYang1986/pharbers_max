package com.pharbers.aqll.alCalcOther.alSchedulerJobs

import java.util.Calendar

import akka.actor.{Actor, ActorLogging, Props}
import com.pharbers.aqll.alCalaHelp.fileConfig._
import com.pharbers.aqll.alCalaHelp.timingConfig._
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
//			printf("Hours: %s, Minute: %s, Seconds: %s \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.SECOND))
			if (hours == time.get(Calendar.HOUR_OF_DAY) &&
				minutes == time.get(Calendar.MINUTE) &&
				seconds > time.get(Calendar.SECOND)) {
				alScheduleRemoveFiles.rmLst foreach (alFileOpt(_).removeCurFiles)
			}
		case _ => ???
	}

	override def receive = remove
}
