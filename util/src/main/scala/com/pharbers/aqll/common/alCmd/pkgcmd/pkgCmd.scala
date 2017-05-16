package com.pharbers.aqll.common.alCmd.pkgcmd

import com.pharbers.aqll.common.alCmd.alCallShellCmdExce.otherFactory
/**
  * Created by Alfred on 09/03/2017.
  */
case class pkgCmd(val lst : List[String], val compress_file : String) {
   val cmd = s"tar -czvf ${compress_file}.tar.gz ${lst.head}"
   otherFactory.CreateShellCmdExce().excute(cmd)
}

