package com.pharbers.aqll.alcalc.alcmd.pyshell

import java.io._

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
            var result = ""
            do {
                line = input.readLine()
                if(line!=null)
                    result = line
            } while (line != null)
            if(!result.equals("")){
                println("Python Code调用成功。")
                python_func(0,result)
            }else{
                println("Python Code调用失败，内部错误。")
                python_func(-1,"error")
            }
        } catch {
            case _ : IOException => {
                println("io exception occurs")
                python_func(-1,"error")
            }
            case ex : Exception => {
                println(ex.getMessage)
                python_func(-1,"error")
            }
        }
    }

    def python_func(status: Integer,result: String) : List[Python] ={
        val python : Python = new Python()
        python.setStatus(status)
        python.setResult(result)
        python :: Nil
    }
}
