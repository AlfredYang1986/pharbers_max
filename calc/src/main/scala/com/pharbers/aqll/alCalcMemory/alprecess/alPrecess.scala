package com.pharbers.aqll.alCalcMemory.alprecess

import com.pharbers.aqll.alCalcEnergy.alAkkaMonitoring.alRegisterCommond
import com.pharbers.aqll.common.alCmd.alShellCmdExce
import com.pharbers.aqll.alCalcMemory.alstages.alStage

/**
  * Created by Alfred on 10/03/2017.
  */

trait alPrecess{
    def precess(j : alStage) : List[alStage]        // 只能用alStorage中的map，要不然整个延迟计算就会失效

    def result : Option[Any] = None
}

trait alFilePrecess {
    def precess(s: alShellCmdExce)
}

trait alRegister {
    def precess(s: alRegisterCommond)
}
