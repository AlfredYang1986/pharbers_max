package com.pharbers.aqll.common.alCmd.pkgcmd

import com.pharbers.aqll.common.alCmd.alShellOtherCmdExce

/**
  * Created by Alfred on 10/03/2017.
  */
case class unPkgCmd(compress_file : String, des_dir : String) extends alShellOtherCmdExce{
  override def cmd = s"tar -xzvf ${compress_file}.tar.gz -C ${des_dir}"
}