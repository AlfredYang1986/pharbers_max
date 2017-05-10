package com.pharbers.aqll.old.client.util

import java.text.SimpleDateFormat
import java.util.Date
/**
  * Created by liwei on 2017/4/1.
  */
object DateUtils {

  /**
    * 将yyyyMM转换为Long
    *
    * @author liwei
    * @param str
    * @return
    */
  def yyyyMM2Long(str: String) : Long = {
    val sdf = new SimpleDateFormat("yyyyMM")
    sdf.parse(str).getTime
  }

  /**
    * 将MMyyyy转换为Long
    *
    * @author liwei
    * @param str
    * @return
    */
  def MMyyyy2Long(str: String) : Long = {
    val sdf = new SimpleDateFormat("MM/yyyy")
    sdf.parse(str).getTime
  }

  /**
    * 通过MMyyyy获取上月Long
    *
    * @author liwei
    * @param str
    * @return
    */
  def MMyyyy2EarlyLong(str: String) : Long = {
    val arr = str.split("/")
    var mont = arr.head.toInt-1
    if(mont==0){ mont = 12 }
    val year = arr.tail.head.toInt
    val newstr = s"$mont/$year"
    val sdf = new SimpleDateFormat("MM/yyyy")
    sdf.parse(newstr).getTime
  }

  /**
    * 通过yyyyMM获取上月Long
    *
    * @author liwei
    * @param str
    * @return
    */
  def yyyyMM2EarlyLong(str: String) : Long = {
    val year = str.substring(0,4)
    var mont = str.substring(4,str.length).toInt-1
    if(mont==0){ mont = 12 }
    val newstr = s"$year$mont"
    val sdf = new SimpleDateFormat("yyyyMM")
    sdf.parse(newstr).getTime
  }

  /**
    * 通过MMyyyy获取去年同期Long
    *
    * @author liwei
    * @param str
    * @return
    */
  def MMyyyy2LastLong(str: String) : Long = {
    val arr = str.split("/")
    var mont = arr.head.toInt
    val year = arr.tail.head.toInt-1
    val newstr = s"$mont/$year"
    val sdf = new SimpleDateFormat("MM/yyyy")
    sdf.parse(newstr).getTime
  }

  /**
    * 通过yyyyMM获取去年同期Long
    *
    * @author liwei
    * @param str
    * @return
    */
  def yyyyMM2LastLong(str: String) : Long = {
    val year = str.substring(0,4).toInt-1
    var mont = str.substring(4,str.length)
    val newstr = s"$year$mont"
    val sdf = new SimpleDateFormat("yyyyMM")
    sdf.parse(newstr).getTime
  }

  /**
    * 通过MMyyyy获取近12月List[Long]
    *
    * @author liwei
    * @param str
    * @return
    */
  def MMyyyy2Early12Long(str: String) : List[Long] = {
    val arr = str.split("/")
    var mont = arr.head.toInt
    val year = arr.tail.head.toInt
    val sdf = new SimpleDateFormat("MM/yyyy")
    sdf.parse(s"${mont+1}/${year-1}").getTime :: sdf.parse(s"${mont+1}/$year").getTime :: Nil
  }

  /**
    * 通过MMyyyy获取去年近12月份List[Long]
    *
    * @author liwei
    * @param str
    * @return
    */
  def MMyyyy2Last12Long(str: String) : List[Long] = {
    val arr = str.split("/")
    var mont = arr.head.toInt
    val year = arr.tail.head.toInt
    val sdf = new SimpleDateFormat("MM/yyyy")
    sdf.parse(s"${mont+1}/${year-2}").getTime :: sdf.parse(s"${mont+1}/${year-1}").getTime :: Nil
  }

  /**
    * 将Long转换为yyyyMM
    *
    * @author liwei
    * @param lon
    * @return
    */
  def Timestamp2yyyyMM(lon : Long) : String = {
    val sdf:SimpleDateFormat = new SimpleDateFormat("yyyyMM")
    sdf.format(new Date(lon))
  }

  /**
    * 将Long转换为yyyy-MM-dd
    *
    * @author liwei
    * @param lon
    * @return
    */
  def Timestamp2yyyyMMdd(lon : Long) : String = {
    val sdf:SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
    sdf.format(new Date(lon))
  }

  /**
    * 将MMyyyy转换为yyyyMM
    *
    * @author liwei
    * @param str
    * @return
    */
  def MMyyyy2yyyyMM(str: String): String ={
    val sdf = new SimpleDateFormat("MM/yyyy")
    Timestamp2yyyyMM(sdf.parse(str).getTime)
  }

  /**
    * 将Array[yyyyMM]转换为Array[Long]
    *
    * @author liwei
    * @param arr
    * @return
    */
  def ArrayDate2ArrayTimeStamp(arr: Array[String]): Array[Long] = {
    arr.map(x => DateUtils.yyyyMM2Long(x))
  }
}
