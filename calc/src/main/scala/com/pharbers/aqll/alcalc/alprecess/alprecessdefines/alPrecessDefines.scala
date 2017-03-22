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
        def apply(uuidOpt : Option[String] = None, prefix : Option[String] = None, nameOpt : Option[String] = None) : alPrecess =
            new alPresistStagePrecess(uuidOpt, prefix, nameOpt)
    }

    object restore_data extends precess_defines(2, "restore data") {
        def apply() : alPrecess = new alRestorePrecess
    }

    object restore_grouped_data extends precess_defines(3, "restore data") {
        def apply() : alPrecess = new alRestoreGroupedPrecess
    }

    object split_data extends precess_defines(4, "split data") {
        def apply(s : alSplitStrategy) : alPrecess = new alSplitPrecess(s)
    }

    object  do_map extends precess_defines(5, "map data") {
        def apply(s : Any => Any) : alPrecess = new alMapPrecess(s)
    }
    
    object do_distinct extends precess_defines(6, "distinct data") {
        def apply() : alPrecess = new alDistinctPrecess
    }
    
    object do_union extends precess_defines(7, "union data") {
        def apply() : alPrecess = new alUnionPrecess
    }

    object do_filter extends precess_defines(8, "filter data") {
        def apply(f : Any => Boolean) = new alFilterPrecess(f)
    }

    object do_calc extends precess_defines(9, "do calc") {
        def apply() : alPrecess = new alCalcPrecess
    }

    object do_pkg extends precess_defines(10, "do pkg") {
        def apply(): alFilePrecess = new alPkgPrecess
    }
}

sealed class precess_defines(val t : Int, val d : String)
