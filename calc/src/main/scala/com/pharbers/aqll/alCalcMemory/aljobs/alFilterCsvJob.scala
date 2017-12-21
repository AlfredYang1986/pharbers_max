package com.pharbers.aqll.alCalcMemory.aljobs

import com.pharbers.alCalcMemory.aljobs.alJob
import com.pharbers.alCalcMemory.alstages.alStage
import com.pharbers.aqll.alCalcHelp.alShareData
import com.pharbers.aqll.alCalcMemory.aljobs.alJobs.max_filter_csv_jobs._
import com.pharbers.aqll.alCalcMemory.alprecess.alprecessdefines.alPrecessDefines.{do_map, restore_data}

/**
  * Created by jeorch on 17-10-12.
  */
class alFilterCsvJob extends alJob {
    def init(args : Map[String, Any]) = {
        val csv_path = args.get(filter_csv_path).map (x => x.toString).getOrElse(throw new Exception("have to provide excel file"))
        cur = Some(alStage(csv_path))
        process = restore_data() :: do_map (alShareData.csv2IntegratedData(_)) :: Nil
    }
}
