package com.pharbers.aqll.calc.util

/**
  * Created by qianpeng on 2017/2/18.
  */
object Const {
	val MANGIC_NUM = 10
	val ATTEMPT_NUM = 3
	val SPLITEXCEL = 10000
	val OUTFILE = GetProperties.loadConf("File.conf").getString("SCP.Out_File")
	var DB = "Max_Cores"
}
