package com.pharbers.aqll.alCalcMemory.aljobs

import com.pharbers.aqll.common.alFileHandler.fileConfig._
import com.pharbers.aqll.alCalcMemory.alprecess.alprecessdefines.alPrecessDefines.{presist_data, restore_data, split_data}
import com.pharbers.aqll.alCalcMemory.alprecess.alsplitstrategy.alSplitStrategy.core_split
import com.pharbers.aqll.alCalcMemory.alstages.alStage

/**
  * Created by Alfred on 11/03/2017.
  */
class alGroupJob(u : String, val parent : String) extends alJob {
    override val uuid: String = u
    val ps = presist_data(Some(uuid))

    def init(args : Map[String, Any]) = {
//        val restore_path = """config/sync/""" + parent + "/" + uuid
        val restore_path = s"${memorySplitFile}${sync}$parent/$uuid"
        cur = Some(alStage(restore_path))
        process = restore_data() :: split_data(core_split(Map(core_split.core_number -> 4))) :: ps :: Nil
    }
    override def result : Option[Any] =  {
        super.result
        ps.result
    }
}