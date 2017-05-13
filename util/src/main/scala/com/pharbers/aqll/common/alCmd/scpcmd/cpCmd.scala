package com.pharbers.aqll.common.alCmd.scpcmd

import com.pharbers.aqll.old.calc.alcalc.alcmd.shellCmdExce


/**
  * Created by Alfred on 10/03/2017.
  */
case class cpCmd(val file : String, val des_path : String) extends shellCmdExce {
    val cmd = s"cp ${file} ${des_path}"
}
