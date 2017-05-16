package com.pharbers.aqll.alcalc.alCommon

import com.google.gson.Gson

/**
  * Created by qianpeng on 2017/5/16.
  */
object alFromJson {
	def apply(resp: String) = formJson(resp)
	def formJson(resp: String) = new Gson().fromJson(resp, classOf[Map[String, String]])
}
