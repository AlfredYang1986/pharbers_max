package com.pharbers.aqll.alCalcMemory.aljobs

import com.pharbers.alCalcMemory.aljobs.alJob
import com.pharbers.alCalcMemory.alstages.alStage
import com.pharbers.aqll.alCalcHelp.alShareData
import com.pharbers.aqll.alCalcMemory.alprecess.alprecessdefines.alPrecessDefines._
import com.pharbers.aqll.common.alFileHandler.fileConfig._

/**
  * Created by Alfred on 11/03/2017.
  */
class alConcertGroupJob(u : String, val parent : String) extends alJob {
    override val uuid: String = u

    def init(args : Map[String, Any]) = {
        val restore_path = s"${memorySplitFile}${sync}$parent/$uuid"
        cur = Some(alStage(restore_path))
        process = restore_data() :: do_map (alShareData.txt2IntegratedData(_)) :: do_calc() :: Nil
    }
}