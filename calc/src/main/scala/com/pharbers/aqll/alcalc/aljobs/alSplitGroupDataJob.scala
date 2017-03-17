package com.pharbers.aqll.alcalc.aljobs

import com.pharbers.aqll.alcalc.aljobs.alJob.grouping_jobs._
import com.pharbers.aqll.alcalc.alprecess.alprecessdefines.alPrecessDefines._
import com.pharbers.aqll.alcalc.alprecess.alsplitstrategy.alSplitStrategy._
import com.pharbers.aqll.alcalc.alprecess.alsplitstrategy.{alServerHardware, server_info}
import com.pharbers.aqll.alcalc.alstages.alStage


class alSplitGroupDataJob(u : String) extends alJob {
    override val uuid: String = u
    val ps = presist_data(Some(uuid), Some("calc"))

    def init(args : Map[String, Any]) = {
        val restore_path = """config/group/""" + uuid
        cur = Some(alStage(restore_path))
        val number = alServerHardware.strategy_hardware(Map(alServerHardware.server_memory -> server_info.memory))(alServerHardware.strategy_memeory)
        process = restore_grouped_data() :: split_data(read_excel_split(Map(read_excel_split.section_number -> number))) :: ps :: Nil
    }
    override def result : Option[Any] =  {
        super.result
        ps.result
    }  
}