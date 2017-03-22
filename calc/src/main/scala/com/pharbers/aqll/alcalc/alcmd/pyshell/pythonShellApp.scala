package com.pharbers.aqll.alcalc.alcmd.pyshell

import com.pharbers.aqll.alcalc.alcmd.pyshell.pyShell
/**
  * Created by liwei on 2017/3/22.
  */
object pythonShellApp extends App{
  //Users/liwei/Downloads/upload/098f6bcd4621d373cade4e832627b4f6/2016/201611CPA.xlsx
  //Users/liwei/Downloads/upload/098f6bcd4621d373cade4e832627b4f6/2016/201611GYCX.xlsx
  try {
    val result = pyShell("098f6bcd4621d373cade4e832627b4f6",List(("201611CPA.xlsx","CPA"),("201611GYCX.xlsx","GYCX")),"2016").excute
    println(s"python_result=${result}")
  } catch {
    case e: Exception => e.printStackTrace()
  }
}
