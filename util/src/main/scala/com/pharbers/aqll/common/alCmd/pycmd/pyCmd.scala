package com.pharbers.aqll.common.alCmd.pycmd

import com.pharbers.aqll.common.alCmd.ShellOtherCmdExce

/**
  * Created by qianpeng on 2017/5/13.
  */
case class pyCmd(pyDir: String, pyFileName: String, args: String, yms: String) extends ShellOtherCmdExce{
	override val cmd = "python" + PyConfig(pyDir, pyFileName, Some(args), yms).toArgs
}
