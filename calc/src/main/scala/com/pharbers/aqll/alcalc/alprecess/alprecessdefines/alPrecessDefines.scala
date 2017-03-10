package com.pharbers.aqll.alcalc.alprecess.alprecessdefines

import com.pharbers.aqll.alcalc.alprecess._

/**
  * Created by Alfred on 10/03/2017.
  */
object alPrecessDefines {
    object read_excel extends precess_defines(0, "read excel") {
        def apply() : alPrecess = new alReadExcelPrecess
    }

    object presist_data extends precess_defines(1, "presist data") {
        def apply() : alPrecess = new alPresistStagePrecess
    }

    object restore_data extends precess_defines(2, "restore data") {
        def apply() : alPrecess = new alRestorePrecess
    }

    object do_calc extends precess_defines(9, "do calc") {
        def apply() : alPrecess = new alCalcPrecess
    }
}

sealed class precess_defines(val t : Int, val d : String)
