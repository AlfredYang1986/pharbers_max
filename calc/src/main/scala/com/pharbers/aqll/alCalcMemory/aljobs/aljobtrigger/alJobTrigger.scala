package com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger

import akka.actor.{ActorRef, ActorSelection}
import com.pharbers.aqll.alCalaHelp.alMaxDefines.{alCalcParmary, alMaxProperty}
import com.pharbers.alCalcMemory.aljobs.alJob

/**
  * Created by BM on 10/03/2017.
  */
object alJobTrigger {

    /**
      * for drivers
      *
      */
    case class worker_register()
    case class push_max_job(path : String, p: alCalcParmary)
    case class push_split_excel_job(path : String, p: alCalcParmary)
    case class push_group_job(property : alMaxProperty)
    case class finish_max_job(uuid : String)
    case class finish_max_group_job(uuid: String)

    case class schedule_jobs()
    case class schedule_group()
    case class schedule_calc()

    case class push_calc_job(p : alMaxProperty)
    case class push_calc_job_2(p : alMaxProperty, c : alCalcParmary)

    case class group_register(a : ActorRef)
    case class calc_register(a : ActorRef)

    case class filter_excel_jobs(file: String, p: alCalcParmary, actorSelection: ActorSelection)
    case class filter_excel_job_2(file : String, p : alCalcParmary)
    case class commit_finalresult_jobs(company: String, uuid: String)
    case class check_excel_jobs(company: String,filename: String)
    /**
      * for split excel
      */
    case class split_job(j : alJob, p: alCalcParmary)
    case class spliting_job(j : alJob, p: alCalcParmary)
    case class spliting_busy()
    case class finish_split_excel_job(p : String, j : List[String], c: alCalcParmary)

    /**
     * nomal concert
     */
    case class concert_adjust()
    case class concert_adjust_result(index : Int)
    case class concert_groupjust_result(index: Int)
    case class concert_calcjust_result(index: Int)
    
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
    case class group_job(j : alMaxProperty, p: alCalcParmary)
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
    case class calc_job(j : alMaxProperty, p: alCalcParmary)
    case class calcing_job(j : List[alJob], r : String)
    case class calc_sum_result(uuid : String, sub_uuid : String, sum : List[(String, (Double, Double, Double))])
    case class calc_avg_job(uuid : String, avg : List[(String, Double, Double)])
    case class calc_final_result(uuid : String, sub_uuid : String, v : Double, u : Double)

    /**
      * for concert calc
      */
    case class concert_calc(p : alMaxProperty, c: alCalcParmary)
    case class concert_calc_sum_result(sub_uuid : String, sum : List[(String, (Double, Double, Double))])
    case class concert_calc_avg(p : alMaxProperty, avg : List[(String, Double, Double)])
    case class concert_calc_result(sub_uuid : String, v : Double, u : Double)

    /**
      * for crash calc and group
      */
    case class crash_calc(uuid: String, msg: String)
    case class crash_group(uuid: String, msg: String)
    case class clean_crash_actor(uuid: String)

    /**
      * for reStart count
      */
    case class canIReStart(reason: Throwable)
    case class canDoRestart(reason: Throwable)
    case class cannotRestart(reason: Throwable)

    /**
      * for restore bson
      */
    case class push_restore_job(coll : String, sub_uuid : String)
}
