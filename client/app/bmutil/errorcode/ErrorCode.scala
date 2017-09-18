package bmutil.errorcode

import play.api.libs.json.Json
import play.api.libs.json.Json._
import play.api.libs.json.JsValue
/**
  * Created by yym on 9/13/17.
  */
object ErrorCode {
  	case class ErrorNode(name : String, code : Int, message : String)

  	private def xls : List[ErrorNode] = List(
		new ErrorNode("input error", -1, "输入的参数有错误"),
		new ErrorNode("url get error", -101, "URL 有误"),
		
		new ErrorNode("User have no email", -201, "无用户邮箱"),
		new ErrorNode("User have no secret", -202, "无用户密码"),
		
		new ErrorNode("unknown error", -999, "unknown error")
	)

	def getErrorCodeByName(name : String) : Int = (xls.find(x => x.name == name)) match {
		case Some(y) => y.code
		case None => -9999
	}

	def getErrorMessageByName(name : String) : String = (xls.find(x => x.name == name)) match {
		case Some(y) => y.message
		case None => "unknow error"
	}

	def errorToJson(name : String) : JsValue =
		Json.toJson(Map("status" -> toJson("error"), "error" ->
			toJson(Map("code" -> toJson(this.getErrorCodeByName(name)), "message" -> toJson(this.getErrorMessageByName(name))))))
}