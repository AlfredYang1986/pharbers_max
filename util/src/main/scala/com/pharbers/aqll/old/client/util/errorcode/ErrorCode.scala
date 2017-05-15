package com.pharbers.aqll.old.client.util.errorcode

import play.api.libs.json.Json
import play.api.libs.json.Json._
import play.api.libs.json.JsValue

object ErrorCode {
    
  	case class ErrorNode(name : String, code : Int, message : String)

  	private def xls : List[ErrorNode] = List(
  		new ErrorNode("token exprie", -1, "inputing token is exprition"),
			new ErrorNode("can not parse result", -20, "can not parse result"),
  		new ErrorNode("unknown error", -999, "unknown error")
  	)

  	def getErrorCodeByName(name : String) : Int = (xls.find(x => x.name == name)) match {
  			case Some(y) => y.code
  			case None => -9999
  		}
  	
   	def getErrorMessageByCode(code : Int) : String = (xls.find(x => x.code == code)) match {
  			case Some(y) => y.message
  			case None => "unknow error"
  		}
   	
   	def getErrorMessageByName(name : String) : String = (xls.find(x => x.name == name)) match {
  			case Some(y) => y.message
  			case None => "unknow error"
  		}
   
   	def errorMessageByCode(code : Int) : (Int, String) = (code, getErrorMessageByCode(code))

		def errorToJson(name : String) : JsValue = {
			Json.toJson(Map("status" -> toJson("error"), "error" ->
			toJson(Map("code" -> toJson(this.getErrorCodeByName(name)), "message" -> toJson(this.getErrorMessageByName(name))))))
		}

}