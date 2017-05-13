package com.pharbers.aqll.common.alErrorHandler

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
	override def table: List[ErrorNode] = Nil
}
