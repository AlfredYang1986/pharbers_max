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
}

sealed class job_defines(val t : Int, val d : String)

trait alJob {
    val uuid = UUID.randomUUID.toString

    var cur : Option[alStage] = None
    var process : List[alPrecess] = Nil
//    var result : Option[Any]

    def init(args : Map[String, Any])
    def result : Option[Any]
    def clean = Unit

    def nextAcc         // 递归实现next
}
