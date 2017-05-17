package com.pharbers.aqll.common.alCmd

import java.io.{InputStreamReader, LineNumberReader}

import com.pharbers.aqll.common.alErrorCode.alErrorCode
import play.api.libs.json.{JsValue, Json}
import play.api.libs.json.Json.toJson

/**
  * Created by liwei on 2017/5/16.
  */

trait alShellCmdExce {
  val cmd = ""

  def excute: JsValue

  /**
    * 目前针对Python文件返回
    * @param m 返回内容
    */
  def resultDefines(m: String) : JsValue = {
    Json.toJson(Map("status" -> toJson("success"), "message" -> toJson(m)))
  }
}

class alShellOtherCmdExce() extends alShellCmdExce {
  override def excute: JsValue = {
    try {
      new ProcessBuilder("/bin/bash", "-c", cmd).start().waitFor()
      alErrorCode.errorToJson("shell success")
    } catch {
      case e : Exception => alErrorCode.errorToJson("shell error")
    }
  }
}

class alShellPythonCmdExce() extends alShellCmdExce {
  override def excute: JsValue = {
    try {
      val process = new ProcessBuilder("/bin/bash", "-c", cmd).start()
      val input = new LineNumberReader(new InputStreamReader(process.getInputStream()))
      var line: String = ""
      process.waitFor()
      val strbuff : StringBuffer = new StringBuffer()
      do {
        line = input.readLine()
        if(!line.isEmpty) strbuff.append(line)
      } while (!line.isEmpty)
      if(strbuff.toString.isEmpty) resultDefines(strbuff.toString)
      else alErrorCode.errorToJson("shell error")
    } catch {
      case e : Exception => alErrorCode.errorToJson("shell error")
    }
  }
}
