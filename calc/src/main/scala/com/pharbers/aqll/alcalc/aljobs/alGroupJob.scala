package com.pharbers.aqll.alcalc.aljobs

import com.pharbers.aqll.alcalc.aljobs.alJob.grouping_jobs._
import com.pharbers.aqll.alcalc.alprecess.alprecessdefines.alPrecessDefines._
import com.pharbers.aqll.alcalc.alprecess.alsplitstrategy.alSplitStrategy.core_split
import com.pharbers.aqll.alcalc.alstages.alStage

/**
  * Created by Alfred on 11/03/2017.
  */
class alGroupJob(u : String, val parent : String) extends alJob {
    override val uuid: String = u
    val ps = presist_data(Some(uuid))

    def init(args : Map[String, Any]) = {
        val restore_path = """config/sync/""" + parent + "/" + uuid
        cur = Some(alStage(restore_path))
        process = restore_data() :: split_data(core_split(Map(core_split.core_number -> 4))) :: ps :: Nil
    }
    override def result : Option[Any] =  {
        super.result
        ps.result
    }
}