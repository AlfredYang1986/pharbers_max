package com.pharbers.aqll.common.alErrorCode

import play.api.libs.json.{JsValue, Json}
import play.api.libs.json.Json.toJson

/**
  * Created by qianpeng on 2017/5/13.
  */

case class ErrorNode(name: String, code: Int, message: String)

trait alErrorCodeTrait {
	def table: List[ErrorNode]

	def getErrorCodeByName(name: String): Int = (table.find(x => x.name == name)) match {
		case Some(x) => x.code
		case None => -9999
	}

	def getErrorMessageByCode(code: Int): String = (table.find(x => x.code == code)) match {
		case Some(x) => x.message
		case None => "unknow error"
	}

	def getErrorMessageByName(name: String): String = (table.find(x => x.name == name)) match {
		case Some(x) => x.message
		case None => "unknow error"
	}

	def errorMessageByCode(code: Int): (Int, String) = (code, getErrorMessageByCode(code))

	def errorToJson(name: String): JsValue = {
		Json.toJson(Map("status" -> toJson("error"), "error" ->
			toJson(Map("code" -> toJson(this.getErrorCodeByName(name)), "message" -> toJson(this.getErrorMessageByName(name))))))
	}
}
