package com.pharbers.aqll.common.alCmd

import java.io.{InputStreamReader, LineNumberReader}
import com.pharbers.aqll.common.alErrorCode.alErrorCode._
import play.api.libs.json.{JsValue, Json}
import play.api.libs.json.Json.toJson

/**
  * Created by liwei on 2017/5/16.
  */

trait alShellCmdExce {
  def cmd = ""

  def excute: JsValue
}

class alShellOtherCmdExce() extends alShellCmdExce {
  override def excute: JsValue = {
    try {
      println(s"cmd=$cmd")
      new ProcessBuilder("/bin/bash", "-c", cmd).start().waitFor()
      toJson(successToJson(toJson("shell success")).get)
    } catch {
      case e : Exception => errorToJson("shell error")
    }
  }
}

class alShellPythonCmdExce() extends alShellCmdExce {
  override def excute: JsValue = {
    try {
      println(s"cmd=$cmd")
      val process = new ProcessBuilder("/bin/bash", "-c", cmd).start()
      val input = new LineNumberReader(new InputStreamReader(process.getInputStream()))
      var line,result: String = ""
      process.waitFor()
      do {
        line = input.readLine()
        if(line != null) result = line
      } while (line != null)

      if(!result.isEmpty){
        toJson(successToJson(toJson(result)).get)
      } else {
        throw new Exception("shell error")
      }

    } catch {
      case e : Exception => errorToJson(e.getMessage)
    }
  }
}
