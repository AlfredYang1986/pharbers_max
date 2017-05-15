package com.pharbers.aqll.common.alCmd

import com.pharbers.aqll.common.alCmd.almodel.alResultDefines

trait shellCmdExce {

    def process : Process = null

    def cmd : String = ""

    def excute : List[alResultDefines]

    def resultDefines(c: Int, n: String, m: String) : List[alResultDefines] = {
        val result : alResultDefines = new alResultDefines()
        result.setCode(c)
        result.setName(n)
        result.setMessage(m)
        result :: Nil
    }
}
