package com.pharbers.aqll.alCalaHelp.alMaxDefines

import java.util.Calendar

import scala.collection.mutable.ListBuffer
import scala.concurrent.stm.Ref

/**
  * Created by BM on 11/03/2017.
  */

case class alMaxCrash(uuid: String)

case class alCalcParmary(var company: String,
                         var uname: String,
                         var uuid: String = "",
                         var market: String = "",
                         var fileName: String = "",
                         var year: Int = 0,
                         maxTimeTry: Int = 3,
                         var faultTimes: Int = 0)

object alCalcParmary {
	val alParmary = Ref(ListBuffer[alCalcParmary]())
}

object startDate {def apply() = System.currentTimeMillis}

object endDate {
	def apply(content: String, startDate: Long) = {
		val endDate = System.currentTimeMillis
		val c = Calendar.getInstance
		c.setTimeInMillis(endDate - startDate)
		println(s"$content 耗时 : ${c.get(Calendar.MINUTE)} 分, ${c.get(Calendar.SECOND)} 秒, ${c.get(Calendar.MILLISECOND)} 毫秒")
	}
}

case class alMaxSignProperty (val signed : Boolean)