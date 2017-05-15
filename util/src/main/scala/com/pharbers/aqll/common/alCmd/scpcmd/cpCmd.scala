package com.pharbers.aqll.common.alCmd.scpcmd

import com.pharbers.aqll.common.alCmd.ShellOtherCmdExce


/**
  * Created by Alfred on 10/03/2017.
  */
case class cpCmd(val file : String, val des_path : String) extends ShellOtherCmdExce {
   override val cmd = s"cp ${file} ${des_path}"
}
