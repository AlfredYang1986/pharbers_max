package com.pharbers.aqll.alcalc.alcmd.pyshell

import java.io._
import scala.collection.mutable.ListBuffer
import collection.JavaConversions._

trait ShellCmdPyExce {

    var process : Process = null
    val cmd : String

    def excute : List[Python] = {
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
                if(line!=null){
                    println(line)
                    line.split("#").asInstanceOf[Array[String]].foreach(x => lst.append(x))
                }
            } while (line != null)
            lst.remove(lst.length-1)
            println("data standardization finish.")
            val python : Python = new Python()
            python.setStatus(0)
            python.setFilename(lst.head)
            python.setMarkets(lst)
            python :: Nil
        } catch {
            case _ : IOException => {
                println("io exception occurs")
                val python : Python = new Python()
                python.setStatus(-1)
                python.setFilename("error")
                python :: Nil
            }

            case ex : Exception => {
                println(ex.getMessage)
                val python : Python = new Python()
                python.setStatus(-1)
                python.setFilename("error")
                python :: Nil
            }
        }
    }
}
