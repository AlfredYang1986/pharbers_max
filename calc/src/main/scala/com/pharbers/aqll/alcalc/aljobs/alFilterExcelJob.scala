package com.pharbers.aqll.alcalc.aljobs

import com.pharbers.aqll.alcalc.aljobs.alJob.max_filter_excel_jobs._
import com.pharbers.aqll.alcalc.alprecess.alprecessdefines.alPrecessDefines._
import com.pharbers.aqll.alcalc.alstages.alStage

/**
  * Created by qianpeng on 24/03/2017.
  */
class alFilterExcelJob extends alJob {

    def init(args : Map[String, Any]) = {
        val excel_file = args.get(filter_excel_path).map (x => x.toString).getOrElse(throw new Exception("have to provide excel file"))
        cur = Some(alStage(excel_file))
        process = read_excel() :: Nil
    }
}
