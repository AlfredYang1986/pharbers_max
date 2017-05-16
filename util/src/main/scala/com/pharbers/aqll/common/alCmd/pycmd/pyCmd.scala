package com.pharbers.aqll.common.alCmd.pycmd

import com.pharbers.aqll.common.alCmd.ShellPythonCmdExce
import com.pharbers.aqll.common.alCmd.alCallShellCmdExce.pythonFactory
/**
  * Created by qianpeng on 2017/5/13.
  */
case class pyCmd(pyDir: String, pyFileName: String, args: String, yms: String) extends ShellPythonCmdExce{
	val cmd = "python" + PyConfig(pyDir, pyFileName, Some(args), yms).toArgs
	pythonFactory.CreateShellCmdExce().excute(cmd)
}