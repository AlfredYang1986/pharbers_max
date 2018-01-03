package com.pharbers.aqll.alCalcMemory.aljobs

import com.pharbers.aqll.alCalcMemory.alprecess.alprecessdefines.alPrecessDefines._
import com.pharbers.alCalcMemory.alprecess.alsplitstrategy.alSplitStrategy._
import com.pharbers.alCalcMemory.alprecess.alsplitstrategy.server_info
import com.pharbers.alCalcMemory.alstages.alStage
import com.pharbers.aqll.common.alFileHandler.fileConfig._
import scala.concurrent.duration._
import akka.util.Timeout
import com.pharbers.alCalcMemory.aljobs.alJob
import com.pharbers.aqll.alCalcHelp.alShareData


class alSplitGroupDataJob(u : String) extends alJob {
    implicit val timeout: Timeout = Timeout(2 seconds)
    override val uuid: String = u
    val ps = presist_data(Some(uuid), Some("calc"))
    
    def init(args : Map[String, Any]) = {
        val restore_path = s"${memorySplitFile + group + uuid}"
        cur = Some(alStage(restore_path))
        process = restore_grouped_data() :: split_data(hash_split(Map(hash_split.core_number-> server_info.cpu,
                                                                      hash_split.mechine_number -> server_info.section.single.get, //query(), //server_info.section.single.get,
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