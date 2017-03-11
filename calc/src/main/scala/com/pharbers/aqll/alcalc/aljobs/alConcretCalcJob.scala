package com.pharbers.aqll.alcalc.aljobs

import com.pharbers.aqll.alcalc.alprecess.alprecessdefines.alPrecessDefines._
import com.pharbers.aqll.alcalc.alprecess.alsplitstrategy.alSplitStrategy.core_split
import com.pharbers.aqll.alcalc.alstages.alStage

/**
  * Created by Alfred on 11/03/2017.
  */
class alConcretCalcJob(u : String, val parent : String) extends alJob {
    override val uuid: String = u
    val ps = presist_data(Some(uuid))

    def init(args : Map[String, Any]) = {
        val restore_path = """config/sync/""" + parent + "/" + uuid
        cur = Some(alStage(restore_path))
        process = restore_data() :: do_calc() :: do_map(x => 1) :: do_calc() :: Nil
    }
    def result : Option[Any] =  {
        if (!process.isEmpty)
            nextAcc
        ps.result
    }
}