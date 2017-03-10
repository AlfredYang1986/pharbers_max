package com.pharbers.aqll.alcalc.almain

import akka.actor.{Actor, ActorLogging}
import akka.actor.ActorContext
import akka.actor.ActorSystem
import akka.actor.Scheduler
import com.pharbers.aqll.alcalc.aljobs.alJob
import com.pharbers.aqll.alcalc.aljobs.aljobtrigger._
import com.pharbers.aqll.alcalc.aljobs.aljobtrigger.alJobTrigger.{push_max_job, schedule_jobs, finish_max_job}

import scala.concurrent.stm.Ref
import scala.concurrent.duration._


/**
  * Created by Alfred on 10/03/2017.
  */
class alMaxDriver extends Actor
                     with ActorLogging {

    override def receive = {
        case push_max_job(file_path) => {

        }

        case schedule_jobs() => {

        }

        case finish_max_job(uuid) => {

        }

        case _ => ???
    }
}

trait alMaxJobsSchedule { this : Actor =>
    val jobs = Ref(List[alJob]())
    val timer = context.system.scheduler.schedule(0 seconds, 1 seconds, self, new schedule_jobs)
}
