package com.pharbers.aqll.alcalc.alcmd.pyshell

import com.pharbers.aqll.calc.util.GetProperties

case class PythonConfig(val company : String) {

    def toArgs : String = {
      GetProperties.loadConf("File.conf").getString("SCP.FileBase_FilePath") + company + "/Python/" + company + ".py " + company
    }
}