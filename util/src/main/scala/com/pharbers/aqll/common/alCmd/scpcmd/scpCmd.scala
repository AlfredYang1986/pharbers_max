package com.pharbers.aqll.common.alCmd.scpcmd

import com.pharbers.aqll.common.alCmd.ShellOtherCmdExce
import com.pharbers.aqll.common.alCmd.alCallShellCmdExce.otherFactory
/**
  * Created by Alfred on 10/03/2017.
  */

case class scpCmd(val file : String, val des_path : String, val host : String, val user_name : String) extends ShellOtherCmdExce {
   val cmd = s"scp ${file} ${user_name}@${host}:~/${des_path}"
   otherFactory.CreateShellCmdExce().excute(cmd)
}
