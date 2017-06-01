package com.pharbers.aqll.alCalcMemory.alprecess

import com.pharbers.aqll.alCalcMemory.alstages.alStage
import com.pharbers.aqll.alCalcOther.alLog.alLoggerMsgTrait
import com.pharbers.aqll.common.alCmd.alShellCmdExce

/**
  * Created by Alfred on 10/03/2017.
  */

trait alPrecess extends alLoggerMsgTrait{
    def precess(j : alStage) : List[alStage]        // 只能用alStorage中的map，要不然整个延迟计算就会失效
    def action(j : alStage)

    def result : Option[Any] = None
}

trait alFilePrecess {
    def precess(s: alShellCmdExce)
}
