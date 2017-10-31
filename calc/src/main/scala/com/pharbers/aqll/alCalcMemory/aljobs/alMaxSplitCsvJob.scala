package com.pharbers.aqll.alCalcMemory.aljobs

import com.pharbers.alCalcMemory.aljobs.alJob
import com.pharbers.alCalcMemory.alprecess.alsplitstrategy.alSplitStrategy.read_excel_split
import com.pharbers.alCalcMemory.alstages.alStage
import com.pharbers.aqll.alCalc.almain.alShareData
import com.pharbers.aqll.alCalcMemory.aljobs.alJob.max_split_csv_jobs._
import com.pharbers.aqll.alCalcMemory.alprecess.alprecessdefines.alPrecessDefines.{do_map, presist_data, restore_data, split_data}

/**
  * Created by jeorch on 17-10-12.
  */
class alMaxSplitCsvJob extends alJob {
    val ps = presist_data(Some(uuid))

    def init(args : Map[String, Any]) = {
        val csv_file = args.get(max_csv_path).map (x => x.toString).getOrElse(throw new Exception("have to provide excel file"))
        cur = Some(alStage(csv_file))
        //        val number = alServerHardware.strategy_hardware(server_info.memory)(alServerHardware.strategy_memeory)
        // TODO: 目前按照机器去分文件，现在按照内存分 在机器不够的情况下 会一直算不了
        //        process = read_excel() :: split_data(read_excel_split(Map(read_excel_split.section_number -> server_info.section.single.get))) :: ps :: Nil
        process = restore_data() :: do_map (alShareData.csv2IntegratedData(_)) :: split_data(read_excel_split(Map(read_excel_split.section_number -> 1))) :: ps :: Nil
    }
    override def result : Option[Any] =  {
        super.result
        ps.result
    }
}
