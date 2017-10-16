package com.pharbers.aqll.module.fopModule

import java.io.File
import java.util.UUID

import com.pharbers.ErrorCode
import play.api.libs.Files.TemporaryFile
import play.api.libs.json.{JsValue, Json}
import play.api.libs.json.Json.toJson
import play.api.mvc.MultipartFormData

object Upload {
	def uploadFile(data : MultipartFormData[TemporaryFile]) : JsValue = {
	    try {
				var lst : List[JsValue] = Nil
				data.files.foreach { x =>
				val uuid = UUID.randomUUID
				val file = new File("files/")
				if(!file.exists()) {
					file.mkdir()
				}
				new TemporaryFile(x.ref.file).moveTo(new File(s"files/$uuid"), true)
						lst = lst :+ toJson(uuid.toString)
				}
				Json.toJson(Map("status" -> toJson("ok"), "result" -> toJson(lst)))
			} catch {
  	    	case ex : Exception =>
		        println(ex)
		        ErrorCode.errorToJson("upload error")
			}
	}
}
