package com.pharbers.aqll.alcalc.aljobs

import com.pharbers.aqll.alcalc.aljobs.alJob.calc_jobs._
import com.pharbers.aqll.alcalc.alprecess.alprecessdefines.alPrecessDefines._
import com.pharbers.aqll.alcalc.alprecess.alsplitstrategy.alSplitStrategy.core_split
import com.pharbers.aqll.alcalc.alstages.alStage

/**
  * Created by BM on 11/03/2017.
  */
class alCalcJob(u : String, val parent : String) extends alJob {
    override val uuid: String = u
    val ps = presist_data(Some(uuid))

    def init(args : Map[String, Any]) = {
        val restore_path = """config/sync/""" + parent + "/" + uuid
        cur = Some(alStage(restore_path))
        process = restore_data() :: split_data(core_split(Map(core_split.core_number -> 4))) :: do_calc() :: ps :: Nil
    }
    def result : Option[Any] =  {
        if (!process.isEmpty)
            nextAcc
        ps.result
    }
}