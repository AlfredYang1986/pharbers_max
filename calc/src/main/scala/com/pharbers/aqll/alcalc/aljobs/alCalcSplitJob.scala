package com.pharbers.aqll.alcalc.aljobs

import com.pharbers.aqll.alcalc.almain.alShareData
import com.pharbers.aqll.alcalc.alprecess.alprecessdefines.alPrecessDefines.{presist_data, restore_data, split_data}
import com.pharbers.aqll.alcalc.alprecess.alsplitstrategy.alSplitStrategy.hash_split
import com.pharbers.aqll.alcalc.alstages.alStage
import com.pharbers.aqll.calc.excel.IntegratedData.IntegratedData

/**
  * Created by Alfred on 13/03/2017.
  */
class alCalcSplitJob(u : String, val parent : String) extends alJob {
    override val uuid: String = u
    val ps = presist_data(Some(uuid), Some("calc"))

    def init(args : Map[String, Any]) = {
        val restore_path = """config/calc/""" + parent + "/" + uuid
        cur = Some(alStage(restore_path))
        process = restore_data() :: split_data(hash_split(Map(hash_split.core_number -> 4,
                    hash_split.hash_func -> hash_func))) :: ps :: Nil
    }
    override def result : Option[Any] =  {
        super.result
        ps.result
    }

    val hash_func : Any => Int = { x =>
        val d = alShareData.txt2IntegratedData(x.asInstanceOf[String])
        (d.getYearAndmonth.toString + d.getMinimumUnitCh).toStream.map (c => c.toInt).sum
    }
}