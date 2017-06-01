package com.pharbers.aqll.common.alErrorCode

import play.api.libs.json.{JsValue, Json}
import play.api.libs.json.Json.toJson

/**
  * Created by qianpeng on 2017/5/17.
  */
case class ErrorNode(name: String, code: Int, message: String)

object alErrorCode {
	val table: List[ErrorNode] = List(
		/**
		  * Model 文件上传与去读
		  */
		ErrorNode("file not found", -1, "未找到文件"),
		ErrorNode("file content error", -2, "文件内容错误"),
		ErrorNode("file unknow error", -3, "文件未知错误"),
		ErrorNode("delete file error", -4, "删除文件异常"),

		/**
		  * 文件分组分发
		  */
		ErrorNode("group not enough", -101, "没有多余的机器进行分组"),
		ErrorNode("group crash", -102, "分组崩溃"),

		/**
		  * 文件计算分发
		  */
		ErrorNode("calc not enough", -201, "没有多余的机器计算"),
		ErrorNode("calc crash", -202, "计算崩溃"),

		/**
		  * 输入框验证
		  */
		ErrorNode("info input user name", -301, "请输入用户名称"),
		ErrorNode("info input account", -302, "请输入账号"),
		ErrorNode("info input password", -303, "请输入密码"),
		ErrorNode("info input phone", -304, "请输入手机号码"),
		ErrorNode("info input company email", -305, "请输入公司联系邮箱"),
		ErrorNode("info input company address", -306, "请输入公司注册地址"),
		ErrorNode("info input market name", -307, "请输入市场名称"),
		ErrorNode("info input company name", -308, "请输入公司名称"),
		ErrorNode("info select markets you want to delete", -309 ,"请选择要删除的市场"),

		/**
		  * 验证
		  */
		ErrorNode("warn email", -401, "输入正确的邮件地址"),
		ErrorNode("warn cell phone", -402, "输入正确的手机号"),
		ErrorNode("warn code", -403, "输入正确的验证码"),
		ErrorNode("warn user not exist", -404, "用户不存在或者密码不正确"),
		ErrorNode("warn email", -405, "输入正确的邮件地址"),
		ErrorNode("warn input", -406, "输入参数有错误"),
		ErrorNode("warn operation success", -407, "操作成功"),
		ErrorNode("warn operation failed", -408, "操作失败"),
		ErrorNode("warn target already exists", -409, "目标已存在"),
		ErrorNode("warn target does not exist", -410, "目标不存在"),
		ErrorNode("warn cpa file delete failed", -411, "cpa文件删除失败"),
		ErrorNode("warn gycx file delete failed", -412, "gycx文件删除失败"),
		ErrorNode("warn aliyun106 scp copy file failed", -413, "aliyun106 scp拷贝文件失败"),
		ErrorNode("warn aliyun50 scp copy file failed", -415, "aliyun50 scp拷贝文件失败"),
		ErrorNode("warn uuid does not exist", -416, "没有匹配的UUID"),
		ErrorNode("warn data does not exist", -417, "没有匹配的数据"),

		/**
		  * Shell状态
		  */
		ErrorNode("shell error" , -501, "shell执行失败"),
		ErrorNode("shell success" , -502, "shell执行成功"),
		
		/**
		  * 错误信息
		  */
		ErrorNode("error input", -601, "你输入的参数不正确"),
		ErrorNode("error excel generation failed", -602, "excel生成失败"),

		/**
		  * 对接第三方Message通信错误
		  */
		ErrorNode("error parameter", -701, "参数错误"),
		ErrorNode("error server", -702, "消息服务器异常"),
		ErrorNode("error client secre", -703, "客户端安全码异常"),
		ErrorNode("error appkey", -704, "应用标识错误"),

		/**
		  * CalcMemory
		  */
		ErrorNode("persist portion cannot map", -801, "持久化部分无法遍历"),
		ErrorNode("persist portion cannot calc length", -802, "持久化部分无法计算大小"),
		ErrorNode("not implement", -803, "没有实现"),
		ErrorNode("instance of alPortionedStorage", -804, "实例化于alPortionedStorage"),
		ErrorNode("only Memory can calc length", -805, "只有内存可以计算大小"),
		ErrorNode("memory stage cannot precess", -806, "已是内存状态无法处理"),
		ErrorNode("not memory stage cannot precess", -807, "不是内存状态无法处理"),
		ErrorNode("not enough memory", -808, "内存不足"),
		ErrorNode("read excel is map precess", -809, "只有map阶段可以读取excel"),

		/**
		  * 功能未实现
		  */
		ErrorNode("not implement", -998, "工程师正在玩命的开发中"),

		/**
		  * 未知错误
		  */
		ErrorNode("unknow error", -9999, "unknow error")
	)

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
		Json.toJson(Map("status" -> toJson("error"), "result" ->
			toJson(Map("code" -> toJson(this.getErrorCodeByName(name)), "message" -> toJson(this.getErrorMessageByName(name))))))
	}

	def successToJson(result: JsValue = toJson("OK"), page: JsValue = toJson("")): Option[Map[String,JsValue]] = {
		Some(Map("status" -> toJson("success"), "result" ->
			toJson(Map("result" -> result, "page" -> page))))
	}
}
