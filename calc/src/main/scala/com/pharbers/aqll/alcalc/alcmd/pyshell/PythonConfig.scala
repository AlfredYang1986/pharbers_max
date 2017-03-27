package com.pharbers.aqll.alcalc.alcmd.pyshell

import com.pharbers.aqll.util.GetProperties._

case class PythonConfig(val company : String) {

    def toArgs : String = {
      filebase + company + python + company + ".py " + company
    }
}