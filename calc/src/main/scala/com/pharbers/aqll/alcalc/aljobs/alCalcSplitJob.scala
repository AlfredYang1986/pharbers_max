package com.pharbers.aqll.alcalc.aljobs

import com.pharbers.aqll.alcalc.alprecess.alprecessdefines.alPrecessDefines.{presist_data, restore_data, split_data}
import com.pharbers.aqll.alcalc.alprecess.alsplitstrategy.alSplitStrategy.core_split
import com.pharbers.aqll.alcalc.alstages.alStage

/**
  * Created by Alfred on 13/03/2017.
  */
class alCalcSplitJob(u : String, val parent : String) extends alJob {
    override val uuid: String = u
    val ps = presist_data(Some(uuid), Some("calc"))

    def init(args : Map[String, Any]) = {
        val restore_path = """config/calc/""" + parent + "/" + uuid
        cur = Some(alStage(restore_path))
        process = restore_data() :: split_data(core_split(Map(core_split.core_number -> 4))) :: ps :: Nil
    }
    override def result : Option[Any] =  {
        super.result
        ps.result
    }
}