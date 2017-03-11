package com.pharbers.aqll.alcalc.aljobs.aljobtrigger

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
    case class clac_can_job()
    case class clac_job(j : alMaxProperty)
    case class clacing_job(j : alJob)
    case class clacing_accept()
    case class clacing_busy()
    case class clacing_can_accept()
}
