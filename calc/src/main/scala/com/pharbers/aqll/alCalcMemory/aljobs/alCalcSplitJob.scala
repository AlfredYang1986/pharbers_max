package com.pharbers.aqll.alCalcMemory.aljobs

import com.pharbers.aqll.alCalcMemory.alprecess.alprecessdefines.alPrecessDefines.{presist_data, restore_data}
import com.pharbers.aqll.alCalcMemory.alstages.alStage
import com.pharbers.aqll.common.alFileHandler.fileConfig._

/**
  * Created by Alfred on 13/03/2017.
  */
class alCalcSplitJob(u : String, val parent : String, val mid : String) extends alJob {
    override val uuid: String = mid
    val ps = presist_data(Some(uuid), Some("calc"), Some(u))

    def init(args : Map[String, Any]) = {
//        val restore_path = s"${memorySplitFile}${calc}$parent/$u"
        val restore_path = "config/calc/" + parent + "/" + u
        cur = Some(alStage(restore_path))
//        process = restore_data() :: split_data(hash_split(Map(hash_split.core_number -> 4,
//                    hash_split.hash_func -> hash_func))) :: ps :: Nil
        process = restore_data() :: ps :: Nil
    }
    override def result : Option[Any] =  {
        super.result
        ps.result
    }

//    val hash_func : Any => Int = { x =>
//        val d = alShareData.txt2IntegratedData(x.asInstanceOf[String])
//        (d.getYearAndmonth.toString + d.getMinimumUnitCh).toStream.map (c => c.toInt).sum
//    }
}