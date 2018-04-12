package com.pharbers.aqll.common

import play.api.libs.json.JsValue

object MergeParallelResult {
	def apply(lst : List[Map[String, JsValue]]) : Map[String, JsValue] = {
		def acc(lst : List[Map[String, JsValue]]) : Map[String, JsValue] = lst match {
			case Nil => Map.empty
			case head :: tail => head ++ acc(tail)
		}
		acc(lst)
	}
}
