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
            var filename = ""
            do {
                line = input.readLine()
                if(line!=null){
                    println(line)
                    filename = line
                }
            } while (line != null)

            if(!filename.equals("")){
                println("Python Code调用成功。")
                python_func(0,filename,filename :: Nil)
            }else{
                println("Python Code调用失败，内部错误。")
                python_func(-1,"error","error" :: Nil)
            }
        } catch {
            case _ : IOException => {
                println("io exception occurs")
                python_func(-1,"error","error" :: Nil)
            }

            case ex : Exception => {
                println(ex.getMessage)
                python_func(-1,"error","error" :: Nil)
            }
        }
    }

    def python_func(status: Integer,filename: String,markets: List[String]) : List[Python] ={
        val python : Python = new Python()
        python.setStatus(status)
        python.setFilename(filename)
        python.setMarkets(markets)
        python :: Nil
    }
}
