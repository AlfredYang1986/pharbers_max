package com.pharbers.aqll.alcalc.alfinaldataprocess

import java.io._
import com.pharbers.aqll.calc.util.GetProperties
/**
  * Created by liwei on 2017/3/27.
  */
object alSampleCheck {
  def alSampleCheck(company : String) {
    val panel : File = new File(GetProperties.loadConf("File.conf").getString("SCP.FileBase_FilePath")+company+"/Output/")
    val filename = panel.listFiles().tail.head
    println(s"filename=${filename.getName}")

    val hospital : File = new File(GetProperties.loadConf("File.conf").getString("SCP.FileBase_FilePath")+company+"/Hospital/")
    val filename1 = hospital.listFiles().tail.head
    println(filename1.getName)
  }
}
