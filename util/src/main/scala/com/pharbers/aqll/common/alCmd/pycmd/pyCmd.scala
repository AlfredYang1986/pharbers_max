package com.pharbers.aqll.common.alCmd.pycmd

import com.pharbers.aqll.common.alCmd.ShellPythonCmdExce

/**
  * Created by qianpeng on 2017/5/13.
  */
case class pyCmd(pyDir: String, pyFileName: String, args: String, yms: String) extends ShellPythonCmdExce{
	override val cmd = "python" + PyConfig(pyDir, pyFileName, Some(args), yms).toArgs
}