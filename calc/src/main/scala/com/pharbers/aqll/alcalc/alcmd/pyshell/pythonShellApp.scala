package com.pharbers.aqll.alcalc.alcmd.pyshell

/**
  * Created by liwei on 2017/3/22.
  */
object pythonShellApp extends App{
  try {
    val result = pyShell("098f6bcd4621d373cade4e832627b4f6").excute
    println(s"python_result=${result}")
  } catch {
    case e: Exception => e.printStackTrace()
  }
}
