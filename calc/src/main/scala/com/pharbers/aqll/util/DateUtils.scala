package com.pharbers.aqll.util

import java.text.SimpleDateFormat
import java.util.Date
/**
  * Created by liwei on 2017/4/1.
  */
object DateUtils {
  def yyyyMM2Long(str: String) : Long = {
    val sdf : SimpleDateFormat = new SimpleDateFormat("yyyyMM")
    sdf.parse(str).getTime
  }

  def Date2Long(date: Date) : Long = {
    val sdf : SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    sdf.parse(sdf.format(date)).getTime
  }
}
