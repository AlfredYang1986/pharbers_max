package com.pharbers.aqll.alcalc.aljobs.aljobtrigger

/**
  * Created by BM on 10/03/2017.
  */
object alJobTrigger {
    case class push_max_job(path : String)
    case class finish_max_job(uuid : String)

    case class schedule_jobs()
}
