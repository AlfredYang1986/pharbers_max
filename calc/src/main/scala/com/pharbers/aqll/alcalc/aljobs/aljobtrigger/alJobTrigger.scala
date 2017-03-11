package com.pharbers.aqll.alcalc.aljobs.aljobtrigger

import akka.actor.ActorRef
import com.pharbers.aqll.alcalc.aljobs.alJob
import com.pharbers.aqll.alcalc.almaxdefines.alMaxProperty

/**
  * Created by BM on 10/03/2017.
  */
object alJobTrigger {

    /**
      * for drivers
      *
      */

    case class worker_register(map: Map[String, String])
    case class push_max_job(path : String)
    case class finish_max_job(uuid : String)

    case class schedule_jobs()
    case class schedule_calc()

    case class calc_register(a : ActorRef)

    /**
      * for split excel
      */
    case class split_job(j : alJob)
    case class spliting_job(j : alJob)
    case class spliting_busy()
    case class finish_split_excel_job(p : String, j : List[String])

    /**
      * for calc
      */
    case class calc_can_job()
    case class calc_job(j : alMaxProperty)
    case class calcing_job(j : alJob)
    case class calcing_accept()
    case class calcing_busy()
    case class calcing_can_accept()

    /**
      * for concert calc
      */
    case class concert_adjust()
    case class concert_adjust_result(index : Int)
    case class concert_calc(p : alMaxProperty)
}
