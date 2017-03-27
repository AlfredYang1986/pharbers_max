package com.pharbers.aqll.util

/**
  * Created by qianpeng on 2017/2/18.
  */
object Const {
	val MANGIC_NUM = 10
	val ATTEMPT_NUM = 3
	val SPLITEXCEL = 5500
	val OUTFILE = GetProperties.loadConf("File.conf").getString("SCP.Out_File")
	var DB = "Max_Cores"
}
