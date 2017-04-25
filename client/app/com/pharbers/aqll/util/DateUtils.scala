package com.pharbers.aqll.util

import java.text.SimpleDateFormat
import java.util.Calendar
/**
  * Created by liwei on 2017/4/1.
  */
object DateUtils {
  //Example 201611 to 1477929600000
  def yyyyMM2Long(str: String) : Long = {
    val sdf = new SimpleDateFormat("yyyyMM")
    sdf.parse(str).getTime
  }

  //Example 11/2016 to 1477929600000
  def MMyyyy2Long(str: String) : Long = {
    val sdf = new SimpleDateFormat("MM/yyyy")
    sdf.parse(str).getTime
  }

  //Example 11/2016 to (10/2016 to 1475251200000)
  def MMyyyy2EarlyLong(str: String) : Long = {
    val arr = str.split("/")
    var mont = arr.head.toInt-1
    if(mont==0){ mont = 12 }
    val year = arr.tail.head.toInt
    val newstr = s"$mont/$year"
    val sdf = new SimpleDateFormat("MM/yyyy")
    sdf.parse(newstr).getTime
  }

  //Example 201611 to (201610 to 1475251200000)
  def yyyyMM2EarlyLong(str: String) : Long = {
    val year = str.substring(0,4)
    var mont = str.substring(4,str.length).toInt-1
    if(mont==0){ mont = 12 }
    val newstr = s"$year$mont"
    val sdf = new SimpleDateFormat("yyyyMM")
    sdf.parse(newstr).getTime
  }

  //Example 11/2016 to (11/2015 to 1446307200000)
  def MMyyyy2LastLong(str: String) : Long = {
    val arr = str.split("/")
    var mont = arr.head.toInt
    val year = arr.tail.head.toInt-1
    val newstr = s"$mont/$year"
    val sdf = new SimpleDateFormat("MM/yyyy")
    sdf.parse(newstr).getTime
  }

  //Example 201611 to (201511 to 1446307200000)
  def yyyyMM2LastLong(str: String) : Long = {
    val year = str.substring(0,4).toInt-1
    var mont = str.substring(4,str.length)
    val newstr = s"$year$mont"
    val sdf = new SimpleDateFormat("yyyyMM")
    sdf.parse(newstr).getTime
  }
  //Example 11/2016 to ([11/2015 - 11/2016] to [1446307200000 - 1477929600000])
  def MMyyyy2Early12Long(str: String) : List[Long] = {
    val arr = str.split("/")
    var mont = arr.head.toInt
    val year = arr.tail.head.toInt
    val sdf = new SimpleDateFormat("MM/yyyy")
    sdf.parse(s"${mont+1}/${year-1}").getTime :: sdf.parse(s"${mont+1}/$year").getTime :: Nil
  }

  //Example 11/2016 to ([11/2014 - 11/2015] to [1446307200000 - 1477929600000])
  def MMyyyy2Last12Long(str: String) : List[Long] = {
    val arr = str.split("/")
    var mont = arr.head.toInt
    val year = arr.tail.head.toInt
    val sdf = new SimpleDateFormat("MM/yyyy")
    sdf.parse(s"${mont+1}/${year-2}").getTime :: sdf.parse(s"${mont+1}/${year-1}").getTime :: Nil
  }

  def Timestamp2yyyyMM(lon : Long) : String = {
    val timeDate = Calendar.getInstance
    timeDate.setTimeInMillis(lon)
    var year = timeDate.get(Calendar.YEAR).toString
    var month = (timeDate.get(Calendar.MONTH)+1).toString
    year + (if(month.length<2){s"0$month"}else{month})
  }

  def MMyyyy2yyyyMM(str: String): String ={
    val sdf = new SimpleDateFormat("MM/yyyy")
    Timestamp2yyyyMM(sdf.parse(str).getTime)
  }

  def ArrayDate2ArrayTimeStamp(arr: Array[String]): Array[Long] = {
    arr.map(x => DateUtils.yyyyMM2Long(x))
  }

}
