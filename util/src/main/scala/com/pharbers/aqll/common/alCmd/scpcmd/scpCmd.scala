package com.pharbers.aqll.common.alCmd.scpcmd

import com.pharbers.aqll.common.alCmd.shellCmdExce


/**
  * Created by Alfred on 10/03/2017.
  */

case class scpCmd(val file : String, val des_path : String, val host : String, val user_name : String) extends shellCmdExce {
    val cmd = s"scp ${file} ${user_name}@${host}:~/${des_path}"
}
