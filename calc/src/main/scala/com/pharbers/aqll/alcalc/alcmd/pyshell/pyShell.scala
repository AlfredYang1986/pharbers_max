package com.pharbers.aqll.alcalc.alcmd.pyshell


/**
  * Created by liwei on 2017/3/22.
  */

case class pyShell(val company : String) extends ShellCmdPyExce {
  val cmd = "python " + PythonConfig(company).toArgs
}
