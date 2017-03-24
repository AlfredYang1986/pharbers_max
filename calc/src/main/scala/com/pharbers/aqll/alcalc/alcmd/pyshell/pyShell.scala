package com.pharbers.aqll.alcalc.alcmd.pyshell

import com.pharbers.aqll.alcalc.alcmd.pyshell.PythonConfig

/**
  * Created by liwei on 2017/3/22.
  */
import com.pharbers.aqll.alcalc.alcmd.pyshell.ShellCmdPyExce

case class pyShell(val company : String) extends ShellCmdPyExce {
  val cmd = "python " + PythonConfig(company).toArgs
}
