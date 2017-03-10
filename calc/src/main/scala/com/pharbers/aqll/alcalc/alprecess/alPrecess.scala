package com.pharbers.aqll.alcalc.alprecess

import com.pharbers.aqll.alcalc.alprecess.alprecessdefines.alPrecessDefines.read_excel
import com.pharbers.aqll.alcalc.alstages.alStage

/**
  * Created by Alfred on 10/03/2017.
  */

trait alPrecess {
    def precess(j : alStage) : List[alStage]        // 只能用alStorage中的map，要不然整个延迟计算就会失效
    def action(j : alStage)

    def result : Option[Any] = None
}
