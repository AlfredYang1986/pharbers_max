package com.pharbers.aqll.common.alErrorCode

/**
  * Created by qianpeng on 2017/5/13.
  */
object alErrorFactory {
	def alServerError: alServer = new alServer
	def alClientError: alClient = new alClient
}

class alServer() extends alErrorCodeTrait{
	override def table: List[ErrorNode] = List(
		ErrorNode("file not found", -1, "upload files not found"),
		ErrorNode("file content error", -2, "upload file content error"),
		ErrorNode("file empty", -3, "upload files is empty"),
		ErrorNode("file unknow error", -4, "upload unknow error"),
		ErrorNode("group not enough", -5, "group not enough use"),
		ErrorNode("group crash", -6, "grouping crash"),
		ErrorNode("calc not enough", -7, "calc not enough use"),
		ErrorNode("calc crash", -8, "to calculate crash")
	)
}

class alClient() extends alErrorCodeTrait{
	override def table: List[ErrorNode] = List(
		ErrorNode("info input company name", -1, "请输入公司名称"),
		ErrorNode("info input user name", -2, "请输入用户名称"),
		ErrorNode("info input account", -3, "请输入账号"),
		ErrorNode("info input password", -4, "请输入密码"),
		ErrorNode("info input phone", -5, "请输入手机号码"),
		ErrorNode("info input company email", -6, "请输入公司联系邮箱"),
		ErrorNode("info input company address", -7, "请输入公司注册地址"),
		ErrorNode("info input market name", -8, "请输入市场名称"),
		ErrorNode("warn input", -100, "输入参数有错误"),
		ErrorNode("warn email", -101, "输入正确的邮件地址"),
		ErrorNode("warn cell phone", -102, "输入正确的手机号"),
		ErrorNode("warn code", -103, "输入正确的验证码"),
		ErrorNode("warn user not exist", -104, "用户不存在或者密码不正确"),
		ErrorNode("warn email", -105, "输入正确的邮件地址"),
		ErrorNode("error input", -200, "你输入的参数不正确"),
		ErrorNode("not implement", -998, "工程师正在玩命的开发中"),
		ErrorNode("unknown error", -999, "unknown error")
	)
}
