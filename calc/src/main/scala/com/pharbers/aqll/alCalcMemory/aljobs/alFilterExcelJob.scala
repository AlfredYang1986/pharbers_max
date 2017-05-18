package com.pharbers.aqll.alCalcMemory.aljobs

import com.pharbers.aqll.alCalcMemory.aljobs.alJob.max_filter_excel_jobs.filter_excel_path
import com.pharbers.aqll.alCalcMemory.alprecess.alprecessdefines.alPrecessDefines.read_excel
import com.pharbers.aqll.alCalcMemory.alstages.alStage

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
