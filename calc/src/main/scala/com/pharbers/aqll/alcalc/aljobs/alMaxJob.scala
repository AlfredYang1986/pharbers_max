package com.pharbers.aqll.alcalc.aljobs

import com.pharbers.aqll.alcalc.aljobs.alJob.max_jobs._
import com.pharbers.aqll.alcalc.alprecess.alprecessdefines.alPrecessDefines._
import com.pharbers.aqll.alcalc.alprecess.alsplitstrategy.alSplitStrategy.read_excel_split
import com.pharbers.aqll.alcalc.alstages.alStage

/**
  * Created by Alfred on 10/03/2017.
  */
class alMaxJob extends alJob {
    val ps = presist_data(Some(uuid))

    def init(args : Map[String, Any]) = {
        val excel_file = args.get(max_excel_path).map (x => x.toString).getOrElse(throw new Exception("have to provide excel file"))
        cur = Some(alStage(excel_file))
        process = read_excel() :: split_data(read_excel_split(Map(read_excel_split.section_number -> 4))) :: do_calc() :: ps :: Nil
    }
    def result : Option[Any] =  {
        if (!process.isEmpty)
            nextAcc
        ps.result
    }
    def nextAcc = {
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
}
