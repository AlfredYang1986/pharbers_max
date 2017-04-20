package com.pharbers.aqll.alcalc.alSchedulerJobs

import java.util.{Calendar, Date}

import akka.actor.{Actor, ActorLogging, Props}
import com.pharbers.aqll.alcalc.alFileHandler.altext.FileOpt
import com.pharbers.aqll.util.{DateUtil, GetProperties}

/**
  * Created by qianpeng on 2017/3/27.
  */

trait alFilePath {
	def root = GetProperties.root

	def program = GetProperties.program

	def calcFile = GetProperties.memorySplitFile

	def calcPath = root + program + calcFile

	def path = root + program

	def convertStr(str: String) = str.substring(0, str.lastIndexOf("/"))
}

case class rmFile()

object alScheduleRemoveFiles extends alFilePath{

	def props = Props[alScheduleRemoveFiles]

	val rmLst = calcPath + convertStr(GetProperties.calc) ::
				calcPath + convertStr(GetProperties.fileTarGz) ::
				calcPath + convertStr(GetProperties.group) ::
				calcPath + convertStr(GetProperties.sync) ::
				path + convertStr(GetProperties.scpPath) ::
				path + convertStr(GetProperties.dumpdb) :: Nil
}

class alScheduleRemoveFiles extends Actor with ActorLogging{

	// TODO : 要删除的目录 SCP、sync、group、dbdump
	val remove: Receive = {
		case rmFile() =>
			val time = Calendar.getInstance
			printf("Hours: %s, Minute: %s, Seconds: %s \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.SECOND))
			if (GetProperties.hours == time.get(Calendar.HOUR_OF_DAY) &&
				GetProperties.minutes == time.get(Calendar.MINUTE) &&
				GetProperties.seconds > time.get(Calendar.SECOND)) {
				alScheduleRemoveFiles.rmLst foreach (FileOpt(_).rmcAllFiles)
			}
		case _ => ???
	}

	override def receive = remove
}
