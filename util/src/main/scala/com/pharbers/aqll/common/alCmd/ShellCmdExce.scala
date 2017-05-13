package com.pharbers.aqll.common.alCmd

import java.io._

sealed class ShellResultDefines (val t : Int, val d : String)

object ShellResult {
    case object success extends ShellResultDefines(0, "success")
    case object failed extends ShellResultDefines(-1, "failed")
    case object runningException extends ShellResultDefines(-2, "runningException")
}

trait shellCmdExce {

    var process : Process = null
    val cmd : String

    def excute : Int = {
        try {
            val builder = new ProcessBuilder("/bin/bash", "-c", cmd)
            val process = builder.start()

            val ir = new InputStreamReader(process.getInputStream())
            val input = new LineNumberReader(ir)

            var line : String = null
            process.waitFor()
            do {
                line = input.readLine()
            } while (line != null)
            if(line.isEmpty) ShellResult.success.t
            else ShellResult.runningException.t
        } catch {
            case _ : IOException => {
                ShellResult.failed.t
            }

            case ex : Exception => {
                ShellResult.failed.t
            }
        }
    }
}
