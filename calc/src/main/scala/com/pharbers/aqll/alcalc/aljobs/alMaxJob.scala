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
        process = read_excel() :: split_data(read_excel_split(Map(read_excel_split.section_number -> 1))) :: ps :: Nil
    }
    def result : Option[Any] =  {
        if (!process.isEmpty)
            nextAcc
        ps.result
    }

}
