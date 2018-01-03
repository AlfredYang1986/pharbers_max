package com.pharbers.aqll.alCalcMemory.aljobs

import com.pharbers.alCalcMemory.aljobs._

/**
  * Created by Alfred on 10/03/2017.
  */
object alJobs {
    object common_jobs extends job_defines(99, "common jobs") {
         def apply() : alCommonJob = new alCommonJob
    }

    object max_filter_excel_jobs extends job_defines(6, "read excel year market") {
        val filter_excel_path = "excel_path"
        def apply(path : String) : alFilterExcelJob = {
            val tmp = new alFilterExcelJob
            tmp.init(Map(filter_excel_path -> path))
            tmp
        }
    }

    object max_filter_csv_jobs extends job_defines(7, "read csv year market") {
        val filter_csv_path = "csv_path"
        def apply(path : String) : alFilterCsvJob = {
            val tmp = new alFilterCsvJob
            tmp.init(Map(filter_csv_path -> path))
            tmp
        }
    }
    
    object max_jobs extends job_defines(0, "max calc") {
        val max_excel_path = "max_excel_path"
        def apply(path : String) : alMaxJob = {
            val tmp = new alMaxJob
            tmp.init(Map(max_excel_path -> path))
            tmp
        }
    }
    object max_split_csv_jobs extends job_defines(8, "max split csv") {
        val max_csv_path = "max_csv_path"
        def apply(path : String) : alMaxSplitCsvJob = {
            val tmp = new alMaxSplitCsvJob
            tmp.init(Map(max_csv_path -> path))
            tmp
        }
    }
    object grouping_jobs extends job_defines(1, "calc go") {
        val max_uuid = "max_uuid"
        val group_uuid = "calc_uuid"
        def apply(m : Map[String, String]) : alGroupJob = {
            val uuid = m.get(group_uuid).map (x => x).getOrElse(throw new Exception("need one uuid"))
            val parent = m.get(max_uuid).map (x => x).getOrElse(throw new Exception("need one parent"))
            val tmp = new alGroupJob(uuid, parent)
            tmp.init(m)
            tmp
        }
    }
    object concert_grouping_jobs extends job_defines(2, "concert calc go") {
        val max_uuid = "max_uuid"
        val group_uuid = "calc_uuid"
        def apply(m : Map[String, String]) : alConcertGroupJob = {
            val uuid = m.get(group_uuid).map (x => x).getOrElse(throw new Exception("need one uuid"))
            val parent = m.get(max_uuid).map (x => x).getOrElse(throw new Exception("need one parent"))
            val tmp = new alConcertGroupJob(uuid, parent)
            tmp.init(m)
            tmp
        }
    }
    object split_group_jobs extends job_defines(3, "split group go") {
        val max_uuid = "max_uuid"
        def apply(m : Map[String, String]) : alSplitGroupDataJob = {
            val uuid = m.get(max_uuid).map (x => x).getOrElse(throw new Exception("need one uuid"))
            val tmp = new alSplitGroupDataJob(uuid)
            tmp.init(m)
            tmp
        }
    }
    object worker_calc_core_split_jobs extends job_defines(4, "split calc to core") {
        val max_uuid = "max_uuid"
        val calc_uuid = "calc_uuid"
        val mid_uuid = "mid_uuid"
        def apply(m : Map[String, String]) : alCalcSplitJob = {
            val uuid = m.get(calc_uuid).map (x => x).getOrElse(throw new Exception("need one uuid"))
            val parent = m.get(max_uuid).map (x => x).getOrElse(throw new Exception("need one parent"))
            val mid = m.get(mid_uuid).map (x => x).getOrElse(throw new Exception("need one parent"))
            val tmp = new alCalcSplitJob(uuid, parent, mid)
            tmp.init(m)
            tmp
        }
    }
    object worker_core_calc_jobs extends job_defines(5, "worker core calc") {
        val max_uuid = "max_uuid"
        val calc_uuid = "calc_uuid"
        def apply(m : Map[String, String]) : alConertCalcJob = {
            val uuid = m.get(calc_uuid).map (x => x).getOrElse(throw new Exception("need one uuid"))
            val parent = m.get(max_uuid).map (x => x).getOrElse(throw new Exception("need one parent"))
            val tmp = new alConertCalcJob(uuid, parent)
            tmp.init(m)
            tmp
        }
    }
}