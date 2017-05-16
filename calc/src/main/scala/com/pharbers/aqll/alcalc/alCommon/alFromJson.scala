package com.pharbers.aqll.alcalc.alCommon

import com.google.gson.Gson
import scala.collection.mutable.Map

/**
  * Created by qianpeng on 2017/5/16.
  */
object alFromJson2 {
	def apply(resp: String) = formJson(resp)
	def formJson(resp: String): Map[String, String] = new Gson().fromJson(resp, classOf[Map[String, String]])
}
