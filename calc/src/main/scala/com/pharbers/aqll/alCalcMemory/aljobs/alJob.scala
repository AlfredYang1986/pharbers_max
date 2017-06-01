package com.pharbers.aqll.alCalcMemory.aljobs

import java.util.UUID

import com.pharbers.aqll.alCalcMemory.alprecess.alPrecess
import com.pharbers.aqll.alCalcMemory.alstages.alStage
import com.pharbers.aqll.alCalcOther.alLog.alLoggerMsgTrait

/**
  * Created by Alfred on 10/03/2017.
  */
object alJob {
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
    
    object max_jobs extends job_defines(0, "max calc") {
        val max_excel_path = "max_excel_path"
        def apply(path : String) : alMaxJob = {
            val tmp = new alMaxJob
            tmp.init(Map(max_excel_path -> path))
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

sealed class job_defines(val t : Int, val d : String)

trait alJob extends alLoggerMsgTrait{
    val uuid = UUID.randomUUID.toString

    var cur : Option[alStage] = None
    var process : List[alPrecess] = Nil

    def init(args : Map[String, Any])
    def clean = Unit
    
    def result : Option[Any] =  {
        if (!process.isEmpty)
            nextAcc
        None
    }

    def nextAcc : Unit = {             // 递归实现next
        if (!process.isEmpty) {
            val p = process.head
            logger.info(s"current precess is $p")
            process = process.tail

            val s = cur.map (x => x).getOrElse(throw new Exception("job needs current stage"))
            logger.info(s"current stage is $s")
            val s1 = p.precess(s).head
            cur = Some(s1)
            logger.info(s"new stage is $s1")
            if (s1.canLength)
                logger.info(s"if calc new stage has ${s1.length} data")

            nextAcc
        }
    }

    /**
      * 拆分job，用户计算
      */
    var subJobs : Option[List[alJob]] = None
}
