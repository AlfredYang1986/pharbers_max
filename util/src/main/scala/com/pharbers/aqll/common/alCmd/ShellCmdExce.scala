package com.pharbers.aqll.common.alCmd

import java.io.{IOException, InputStreamReader, LineNumberReader}
import com.pharbers.aqll.common.alCmd.almodel.alResultDefines
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

/**
  * Created by liwei on 2017/5/16.
  */

trait alShellCmdExce {
  def process : Process = null

  val cmd = ""

  def excute : List[alResultDefines]

  def resultDefines(c: Int, n: String, m: String) : JsValue = {
    toJson(Map("code" -> toJson(c),"name" -> toJson(n),"message" -> toJson(m)) :: Nil)
  }
}

class alShellOtherCmdExce() extends alShellCmdExce {
  override def excute : JsValue = {
    try {
      new ProcessBuilder("/bin/bash", "-c", cmd).start().waitFor()
      resultDefines(0,"success","")
    } catch {
      case _ : IOException => resultDefines(-2,"error","IOException")
      case ex : Exception => resultDefines(-2,"error","Exception")
    }
  }
}

class alShellPythonCmdExce() extends alShellCmdExce {
  override def excute : JsValue = {
    try {
      val builder = new ProcessBuilder("/bin/bash", "-c", cmd)
      val process = builder.start()
      val ir = new InputStreamReader(process.getInputStream())
      val input = new LineNumberReader(ir)
      var line : String = null
      process.waitFor()
      val strbuff : StringBuffer = new StringBuffer()
      do {
        line = input.readLine()
        if(line!=null)
          strbuff.append(line)
      } while (line != null)

      strbuff.toString match {
        case "" => resultDefines(0,"success",strbuff.toString)
        case _ => resultDefines(-1,"faild","")
      }
    } catch {
      case _ : IOException => resultDefines(-2,"error","IOException")
      case ex : Exception => resultDefines(-2,"error","Exception")
    }
  }
}
