package com.pharbers.aqll.alcalc.aljobs

import com.pharbers.aqll.alcalc.almain.alShareData
import com.pharbers.aqll.alcalc.alprecess.alprecessdefines.alPrecessDefines.{do_calc, do_map, presist_data, restore_data}
import com.pharbers.aqll.alcalc.alstages.alStage
import com.pharbers.aqll.alcalc.alCommon.fileConfig._

/**
  * Created by Alfred on 13/03/2017.
  */
class alConertCalcJob(u : String, val parent : String) extends alJob {
    override val uuid: String = u

    def init(args : Map[String, Any]) = {
//        val restore_path = """config/calc/""" + parent + "/" + uuid
        val restore_path = s"${memorySplitFile}${calc}$parent/$uuid"
        cur = Some(alStage(restore_path))
        process = restore_data() :: do_map (alShareData.txt2IntegratedData(_)) :: do_calc() :: Nil
    }
}