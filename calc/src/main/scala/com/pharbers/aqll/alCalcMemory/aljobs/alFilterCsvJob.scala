package com.pharbers.aqll.alCalcMemory.aljobs

import com.pharbers.aqll.alCalc.almain.alShareData
import com.pharbers.aqll.alCalcMemory.aljobs.alJob.max_filter_csv_jobs._
import com.pharbers.aqll.alCalcMemory.alprecess.alprecessdefines.alPrecessDefines.{do_map, restore_data}
import com.pharbers.aqll.alCalcMemory.alstages.alStage

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
