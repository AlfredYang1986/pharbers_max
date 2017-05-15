package com.pharbers.aqll.common.alCmd

import java.io.{IOException, InputStreamReader, LineNumberReader}
import com.pharbers.aqll.common.alCmd.almodel.alResultDefines

/**
  * Created by liwei on 2017/5/15.
  */
trait ShellOtherCmdExce extends ShellCmdExce{

  override def excute : List[alResultDefines] = {
    try {
      println(cmd)
      val builder = new ProcessBuilder("/bin/bash", "-c", cmd)
      val process = builder.start()
      val ir = new InputStreamReader(process.getInputStream())
      val input = new LineNumberReader(ir)
      var line : String = null
      process.waitFor()
      resultDefines(0,"success","")
    } catch {
      case _ : IOException => resultDefines(-2,"error","IOException")
      case ex : Exception => resultDefines(-2,"error","Exception")
    }
  }
}
