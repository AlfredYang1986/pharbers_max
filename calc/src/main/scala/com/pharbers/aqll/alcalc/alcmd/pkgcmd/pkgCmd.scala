package com.pharbers.aqll.alcalc.alcmd.pkgcmd

import com.pharbers.aqll.alcalc.alcmd.shellCmdExce
/**
  * Created by Alfred on 09/03/2017.
  */
case class pkgCmd(val lst : List[String]) extends shellCmdExce {
    val cmd = "ls "
}

