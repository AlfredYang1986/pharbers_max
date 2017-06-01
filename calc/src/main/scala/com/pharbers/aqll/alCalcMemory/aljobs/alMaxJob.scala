package com.pharbers.aqll.alCalcMemory.aljobs

import com.pharbers.aqll.alCalcMemory.aljobs.alJob.max_jobs._
import com.pharbers.aqll.alCalcMemory.alprecess.alprecessdefines.alPrecessDefines.{presist_data, read_excel, split_data}
import com.pharbers.aqll.alCalcMemory.alprecess.alsplitstrategy.alSplitStrategy.read_excel_split
import com.pharbers.aqll.alCalcMemory.alprecess.alsplitstrategy.server_info
import com.pharbers.aqll.alCalcMemory.alstages.alStage

/**
  * Created by Alfred on 10/03/2017.
  */
class alMaxJob extends alJob {
    val ps = presist_data(Some(uuid))

    def init(args : Map[String, Any]) = {
        val excel_file = args.get(max_excel_path).map (x => x.toString).getOrElse(throw new Exception("have to provide excel file"))
        cur = Some(alStage(excel_file))
//        val number = alServerHardware.strategy_hardware(server_info.memory)(alServerHardware.strategy_memeory)
        process = read_excel() :: split_data(read_excel_split(Map(read_excel_split.section_number -> server_info.section.single.get))) :: ps :: Nil
//        process = read_excel() :: split_data(core_split(Map(core_split.core_number -> server_info.section.single.get))) :: ps :: Nil
    }
    override def result : Option[Any] =  {
        super.result
        ps.result
    }
}
