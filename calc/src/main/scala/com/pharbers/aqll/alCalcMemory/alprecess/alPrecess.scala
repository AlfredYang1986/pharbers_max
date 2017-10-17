package com.pharbers.aqll.alCalcMemory.alprecess

import com.pharbers.alCalcMemory.alstages.alStage
import com.pharbers.aqll.alCalcEnergy.alAkkaMonitoring.alRegisterCommond
import com.pharbers.aqll.common.alCmd.alShellCmdExce

/**
  * Created by Alfred on 10/03/2017.
  */
trait alFilePrecess {
    def precess(s: alShellCmdExce)
}

trait alRegister {
    def precess(s: alRegisterCommond)
}
