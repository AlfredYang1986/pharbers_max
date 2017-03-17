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
    case class worker_register(map: Map[String, Any])
    case class push_max_job(path : String)
    case class finish_max_job(uuid : String)

    case class schedule_jobs()
    case class schedule_group()
    case class schedule_calc()

    case class push_calc_job(p : alMaxProperty)

    case class group_register(a : ActorRef)
    case class calc_register(a : ActorRef)

    /**
      * for split excel
      */
    case class split_job(j : alJob)
    case class spliting_job(j : alJob)
    case class spliting_busy()
    case class finish_split_excel_job(p : String, j : List[String])

    /**
     * nomal concert
     */
    case class concert_adjust()
    case class concert_adjust_result(index : Int)
    
    /**
     * for sign jobs
     */
    case class can_sign_job()
    case class sign_job_accept()
    case class service_is_busy()
    case class sign_job_can_accept()
    
    /**
      * for group
      */
    case class group_job(j : alMaxProperty)
    case class grouping_job(j : alJob)
   
    case class group_result(uuid : String, sub_uuid : String)

    /**
      * for concert group
      */
    case class concert_group(p : alMaxProperty)
    case class concert_group_result(sub_uuid : String)
    
    /**
     * for calc
     */
    case class calc_need_files(uuid_file_path: String)
    case class calc_job(j : alMaxProperty)
    case class calcing_job(j : alJob)
    case class calc_sum_result(uuid : String, sub_uuid : String, sum : List[(String, (Double, Double, Double))])
    case class calc_avg_job(uuid : String, avg : List[(String, Double, Double)])
    case class calc_final_result(uuid : String, sub_uuid : String, v : Double, u : Double)

    /**
      * for concert calc
      */
    case class concert_calc(p : alMaxProperty)
    case class concert_calc_sum_result(sub_uuid : String, sum : List[(String, (Double, Double, Double))])
    case class concert_calc_avg(p : alMaxProperty, avg : List[(String, Double, Double)])
    case class concert_calc_result(sub_uuid : String, v : Double, u : Double)
}
