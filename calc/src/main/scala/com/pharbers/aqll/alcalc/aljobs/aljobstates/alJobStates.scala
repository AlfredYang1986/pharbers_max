package com.pharbers.aqll.alcalc.aljobs.aljobstates

/**
  * Created by Alfred on 10/03/2017.
  */

trait alJobStates {
    var cur_state : alPointState = alMasterJobIdle
}

trait alMaxJobStates extends alJobStates {
    case object resource_waiting extends alPointState
    case object split_excel extends alPointState
    case object max_calcing extends alPointState    // 数据计算结果写入数据库中
}

trait alExcelSplitJobStates extends alJobStates {
    case object excel_split_idleing extends alPointState
    case object spliting_data extends alPointState
}

trait alMaxCalcJobStates extends alJobStates {
    case object calc_idleing extends alPointState
    case object calc_maxing extends alPointState
    case object calc_average extends alPointState
    case object calc_lasting extends alPointState
}
