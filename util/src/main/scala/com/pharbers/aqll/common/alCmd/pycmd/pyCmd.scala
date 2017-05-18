package com.pharbers.aqll.common.alCmd.pycmd

import com.pharbers.aqll.common.alCmd.alShellPythonCmdExce

/**
  * Created by qianpeng on 2017/5/13.
  */
case class pyCmd(pyDir: String, pyFileName: String, arg: String) extends alShellPythonCmdExce {
	override val cmd = "python " + PyConfig(pyDir, pyFileName, Some(arg)).toArgs
}