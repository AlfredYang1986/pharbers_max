package com.pharbers.aqll.alCalcEnergy

import akka.actor.Actor
import akka.routing.RoundRobinPool
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alCalcParmary
import com.pharbers.aqll.alCalc.almain.alExcelSplitActor
import com.pharbers.aqll.alCalcMemory.aljobs.alJob
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger.schedule_jobs

import scala.concurrent.stm.Ref
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by qianpeng on 2017/5/17.
  */
trait alMaxJobsSchedule { this: Actor =>
	val jobs = Ref(List[(alJob, alCalcParmary)]())       // only unhandled jobs 带参数的jobs
	val timer = context.system.scheduler.schedule(0 seconds, 1 seconds, self, new schedule_jobs)
}

trait alCreateExcelSplitRouter { this : Actor =>
	def CreateExcelSplitRouter =
		context.actorOf(RoundRobinPool(1).props(alExcelSplitActor.props), name = "excel-split-router")
}
