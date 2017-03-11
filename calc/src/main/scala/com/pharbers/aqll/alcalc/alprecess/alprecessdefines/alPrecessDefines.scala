package com.pharbers.aqll.alcalc.alprecess.alprecessdefines

import com.pharbers.aqll.alcalc.alprecess._
import com.pharbers.aqll.alcalc.alprecess.alsplitstrategy.alSplitStrategy

/**
  * Created by Alfred on 10/03/2017.
  */
object alPrecessDefines {
    object read_excel extends precess_defines(0, "read excel") {
        def apply() : alPrecess = new alReadExcelPrecess
    }

    object presist_data extends precess_defines(1, "presist data") {
        def apply(uuidOpt : Option[String] = None) : alPrecess = new alPresistStagePrecess(uuidOpt)
    }

    object restore_data extends precess_defines(2, "restore data") {
        def apply() : alPrecess = new alRestorePrecess
    }

    object split_data extends precess_defines(3, "split data") {
        def apply(s : alSplitStrategy) : alPrecess = new alSplitPrecess(s)
    }

    object do_calc extends precess_defines(9, "do calc") {
        def apply() : alPrecess = new alCalcPrecess
    }
}

sealed class precess_defines(val t : Int, val d : String)
