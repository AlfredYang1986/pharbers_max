package com.pharbers.aqll.alcalc.almaxdefines


import scala.collection.mutable.ListBuffer
import scala.concurrent.stm.Ref

/**
  * Created by BM on 11/03/2017.
  */

case class alCalcParmary(var company: String,
                         var uname: String,
                         var uuid: String = "",
                         var market: String = "",
                         var year: Int = 0,
                         var maxTimeTry: Int = 3,
                         var faultTimes: Int = 0)

object alCalcParmary {
	val alParmary = Ref(ListBuffer[alCalcParmary]())
}

object startDate {def apply() = System.currentTimeMillis}

object endDate {
	def apply(content: String, startDate: Long) = {
		val endDate = System.currentTimeMillis
		println(s"$content 耗时 ${((endDate - startDate) / 1000)} 秒")
	}
}

case class alMaxSignProperty (val signed : Boolean)