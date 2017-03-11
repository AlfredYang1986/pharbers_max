package com.pharbers.aqll.alcalc.aljobs

import java.util.UUID

import com.pharbers.aqll.alcalc.alprecess.alPrecess
import com.pharbers.aqll.alcalc.alstages.alStage

/**
  * Created by Alfred on 10/03/2017.
  */
object alJob {
    object max_jobs extends job_defines(0, "max calc") {
        val max_excel_path = "max_excel_path"
        def apply(path : String) : alMaxJob = {
            val tmp = new alMaxJob
            tmp.init(Map(max_excel_path -> path))
            tmp
        }
    }
    object calc_jobs extends job_defines(1, "calc go") {
        val max_uuid = "max_uuid"
        val calc_uuid = "calc_uuid"
        def apply(m : Map[String, String]) : alCalcJob = {
            val uuid = m.get(calc_uuid).map (x => x).getOrElse(throw new Exception("need one uuid"))
            val parent = m.get(calc_uuid).map (x => x).getOrElse(throw new Exception("need one parent"))
            val tmp = new alCalcJob(uuid, parent)
            tmp.init(m)
            tmp
        }
    }
}

sealed class job_defines(val t : Int, val d : String)

trait alJob {
    val uuid = UUID.randomUUID.toString

    var cur : Option[alStage] = None
    var process : List[alPrecess] = Nil

    def init(args : Map[String, Any])
    def result : Option[Any]
    def clean = Unit

    def nextAcc : Unit = {             // 递归实现next
        if (!process.isEmpty) {
            val p = process.head
            println(s"current precess is $p")
            process = process.tail

            val s = cur.map (x => x).getOrElse(throw new Exception("job needs current stage"))
            println(s"current stage is $s")
            val s1 = p.precess(s).head
            cur = Some(s1)
            println(s"new stage is $s1")
            if (s1.canLength)
                println(s"if calc new stage has ${s1.length} data")

            nextAcc
        }
    }

    /**
      * 拆分job，用户计算
      */
    var subJobs : Option[List[alJob]] = None
}
