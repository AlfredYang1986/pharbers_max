package com.pharbers.aqll.common.sercurity

import java.security._
import java.util.Date
import scala.Array.canBuildFrom

object Sercurity {
	def md5Hash(text: String) : String = java.security.MessageDigest.getInstance("MD5").digest(text.getBytes()).map(0xFF & _).map { "%02x".format(_) }.foldLeft(""){_ + _}
	def getTimeSpanWithMillSeconds : String = String.valueOf(Math.floor(new Date().getTime()))
	def getTimeSpanWithSeconds : String = String.valueOf(Math.floor(new Date().getTime() / 1000))
	def getTimeSpanWithMinutes : String = String.valueOf(Math.floor(new Date().getTime() / (1000 * 60)))
	def getTimeSpanWith10Minutes : String = String.valueOf(Math.floor(new Date().getTime() / (1000 * 60 * 10)))
	
	def getTimeSpanWithPast10Minutes : List[String] = {
		val m = new Date().getTime() / (1000 * 60)
		(0 to 9).map { tmp => 
			String.valueOf(Math.floor(m - tmp))
		}.toList
	}
}