package com.pharbers.aqll.alcalc.aljobs

import com.pharbers.aqll.alcalc.aljobs.alJob.grouping_jobs._
import com.pharbers.aqll.alcalc.almain.alShareData
import com.pharbers.aqll.alcalc.alprecess.alprecessdefines.alPrecessDefines._
import com.pharbers.aqll.alcalc.alprecess.alsplitstrategy.alSplitStrategy._
import com.pharbers.aqll.alcalc.alprecess.alsplitstrategy.server_info
import com.pharbers.aqll.alcalc.alstages.alStage
import com.pharbers.aqll.util.GetProperties._


class alSplitGroupDataJob(u : String) extends alJob {
    override val uuid: String = u
    val ps = presist_data(Some(uuid), Some("calc"))

    def init(args : Map[String, Any]) = {
        val restore_path = s"${memorySplitFile}${group}$uuid"
        cur = Some(alStage(restore_path))
        process = restore_grouped_data() :: split_data(hash_split(Map(hash_split.core_number-> server_info.cpu,
                                                                      hash_split.mechine_number -> server_info.section.single.get,
                                                                      hash_split.hash_func -> hash_func))) :: ps :: Nil
        // TODO : 假定每个机器都是一样，这里理论上是直接对洗牌后占用的核数编程，然后对计算机核总数分配，这个叫洗牌，也就是简单的hash
        // 我让你想的东西就在这里
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