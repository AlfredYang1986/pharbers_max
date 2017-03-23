package com.pharbers.aqll.alcalc.alcmd.pyshell

import java.io._
import scala.collection.mutable.ListBuffer

trait ShellCmdPyExce {

    var process : Process = null
    val cmd : String

    def excute : (Int,List[String]) = {
        try {
            println(cmd)
            val builder = new ProcessBuilder("/bin/bash", "-c", cmd)
            val process = builder.start()

            val ir = new InputStreamReader(process.getInputStream())
            val input = new LineNumberReader(ir)
            var line : String = null
            process.waitFor()
            val lst : ListBuffer[String] = new ListBuffer[String]()
            do {
                line = input.readLine()
                if(line!=null)
                    line.split("#").asInstanceOf[Array[String]].foreach(x => lst.append(x))
            } while (line != null)
            lst.remove(lst.length-1)
            println("data standardization finish.")
            (0,lst.toList)
        } catch {
            case _ : IOException => {
                println("io exception occurs")
                (-1,List("faild"))
            }

            case ex : Exception => {
                println(ex.getMessage)
                (-1,List("faild"))
            }
        }
    }
}
