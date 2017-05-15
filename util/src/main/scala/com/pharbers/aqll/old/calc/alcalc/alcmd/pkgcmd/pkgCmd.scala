package com.pharbers.aqll.old.calc.alcalc.alcmd.pkgcmd

import com.pharbers.aqll.old.calc.alcalc.alcmd.shellCmdExce

/**
  * Created by Alfred on 09/03/2017.
  */
case class pkgCmd(val lst : List[String], val compress_file : String) extends shellCmdExce {
    val cmd = s"tar -czvf ${compress_file}.tar.gz ${lst.head}"
}

