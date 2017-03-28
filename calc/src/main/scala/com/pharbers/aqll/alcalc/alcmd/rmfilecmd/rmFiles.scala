package com.pharbers.aqll.alcalc.alcmd.rmfilecmd

import com.pharbers.aqll.alcalc.alcmd.shellCmdExce

/**
  * Created by qianpeng on 2017/3/28.
  */
case class rmFiles(file: String) extends shellCmdExce{
	val cmd = s"rm -r -f ${file}"
}
