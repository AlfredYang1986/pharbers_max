package com.pharbers.aqll.common.alCmd.scpcmd

import com.pharbers.aqll.common.alCmd.alShellOtherCmdExce

/**
  * Created by Alfred on 10/03/2017.
  */
case class cpCmd(file : String, des_path : String) extends alShellOtherCmdExce{
  override def cmd = s"cp ${file} ${des_path}"
}
