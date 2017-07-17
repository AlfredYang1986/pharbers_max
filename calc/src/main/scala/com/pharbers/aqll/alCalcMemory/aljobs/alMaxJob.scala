package com.pharbers.aqll.alCalcMemory.aljobs

import com.pharbers.aqll.alCalcMemory.aljobs.alJob.max_jobs._
import com.pharbers.aqll.alCalcMemory.alprecess.alprecessdefines.alPrecessDefines._
import com.pharbers.aqll.alCalcMemory.alprecess.alsplitstrategy.alSplitStrategy.{core_split, read_excel_split}
import com.pharbers.aqll.alCalcMemory.alprecess.alsplitstrategy.{alServerHardware, alSplitStrategy, server_info}
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
        // TODO: 目前按照机器去分文件，现在按照内存分 在机器不够的情况下 会一直算不了
        process = read_excel() :: split_data(read_excel_split(Map(read_excel_split.section_number -> server_info.section.single.get))) :: ps :: Nil
    }
    override def result : Option[Any] =  {
        super.result
        ps.result
    }
}
