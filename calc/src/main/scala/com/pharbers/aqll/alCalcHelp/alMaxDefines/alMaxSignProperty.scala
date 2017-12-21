package com.pharbers.aqll.alCalcHelp.alMaxDefines

import java.util.Calendar
import scala.concurrent.stm.Ref
import scala.collection.mutable.ListBuffer

/**
  * Created by BM on 11/03/2017.
  */

//TODO shan chu
case class alCalcParmary(var company: String,
						 var imuname: String,
						 var uid: String = "",
						 var uuid: String = "",
						 var market: String = "",
						 var fileName: String = "",
						 var year: Int = 0,
						 maxTimeTry: Int = 3,
						 var faultTimes: Int = 0
                        )
//TODO shanchu
object alCalcParmary {
	val alParmary = Ref(ListBuffer[alCalcParmary]())
}

object startDate {
	def apply(): Long = System.currentTimeMillis
}

object endDate {
	def apply(content: String, startDate: Long): Unit = {
		val endDate = System.currentTimeMillis
		val c = Calendar.getInstance
		c.setTimeInMillis(endDate - startDate)
		println(s"$content 耗时 : ${c.get(Calendar.MINUTE)} 分, ${c.get(Calendar.SECOND)} 秒, ${c.get(Calendar.MILLISECOND)} 毫秒")
	}
}