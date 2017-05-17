package com.pharbers.aqll.old.calc.alcalc.alcmd.pyshell

/**
  * Created by liwei on 2017/3/22.
  */

case class pyShell(val company : String,val filename : String,yms : String) extends ShellCmdPyExce {
  val cmd = "python " + PythonConfig(company,filename,yms).toArgs
}