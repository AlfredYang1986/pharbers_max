package com.pharbers.aqll.alCalcOther.alRemoveJobs

import java.util.Calendar

import akka.actor.{Actor, ActorLogging, Props}
import com.pharbers.aqll.common.alErrorCode.alErrorCode
import com.pharbers.aqll.common.alFileHandler.fileConfig._
import com.pharbers.aqll.common.alFileHandler.timingConfig._
import com.pharbers.aqll.common.alFileHandler.alFilesOpt.alFileOpt

/**
  * Created by qianpeng on 2017/3/27.
  */
// TODO xuyao chongxie
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
	import alScheduleRemoveFiles._
	val remove: Receive = {
		case rmFile() => {
			val time = Calendar.getInstance
			if (hours.toInt == time.get(Calendar.HOUR_OF_DAY) &&
				minutes.toInt == time.get(Calendar.MINUTE) &&
				seconds.toInt > time.get(Calendar.SECOND)) {
				rmLst foreach { x =>
					if(!alFileOpt(x).removeCurFiles) {
						log.error(alErrorCode.errorToJson("delete file error").toString)
					}
				}
			}
		}
		case _ => ???
	}

	override def receive = remove
}
