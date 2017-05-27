package com.pharbers.aqll.common.alCmd.scpcmd

import com.pharbers.aqll.common.alCmd.alShellOtherCmdExce

/**
  * Created by Alfred on 10/03/2017.
  */

case class scpCmd(file : String, des_path : String, host : String, user_name : String) extends alShellOtherCmdExce {
  override def cmd = s"scp ${file} ${user_name}@${host}:~/${des_path}"
}
