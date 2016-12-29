package com.pharbers.aqll.module.fopModule

import java.io.File
import play.api.libs.Files
import java.io.FileInputStream

import play.api.mvc.MultipartFormData
import play.api.libs.Files.TemporaryFile

import play.api.libs.json.Json
import play.api.libs.json.Json._
import play.api.libs.json.JsValue
import java.util.UUID

object fop {
	def uploadFile(data : MultipartFormData[TemporaryFile])(implicit error_handler : Int => JsValue) : JsValue = {
	    try {
  	      	var lst : List[JsValue] = Nil
      	    data.files.foreach { x =>  
      	        val uuid = UUID.randomUUID
      	  	  	Files.moveFile(x.ref.file, new File("upload/" + uuid), true, true)
      	  	  	lst = lst :+ toJson(uuid.toString)
      	  	}
      	    Json.toJson(Map("status" -> toJson("ok"), "result" -> toJson(lst)))
  	    } catch {
  	    	case ex : Exception => error_handler(-1)
  	    }
	}

	def downloadFile(name : String) : Array[Byte] = {
	  	val file = new File("upload/" + name)
		val reVal : Array[Byte] = new Array[Byte](file.length.intValue)
		new FileInputStream(file).read(reVal)
		reVal
	}
}