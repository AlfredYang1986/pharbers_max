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

import com.pharbers.aqll.util.GetProperties

object fop {
	def uploadFile(data : MultipartFormData[TemporaryFile])(implicit error_handler : Int => JsValue) : JsValue = {
	    try {
  	      	var lst : List[JsValue] = Nil
      	    data.files.foreach { x =>
      	        val uuid = UUID.randomUUID
				new TemporaryFile(x.ref.file).moveTo(new File(GetProperties.Client_Upload_FilePath + uuid), true)
      	  	  	lst = lst :+ toJson(uuid.toString)
      	  	}
      	    Json.toJson(Map("status" -> toJson("ok"), "result" -> toJson(lst)))
  	    } catch {
  	    	case ex : Exception => error_handler(-1)
  	    }
	}

	def copyLinux() = {
		val cmd = """sh cp.sh"""
		Runtime.getRuntime.exec(cmd)
	}

	def downloadFile(name : String) : Array[Byte] = {
	  	val file = new File(GetProperties.Client_Download_FilePath + name)
			val reVal : Array[Byte] = new Array[Byte](file.length.intValue)
			new FileInputStream(file).read(reVal)
			reVal
	}

	def exportFile(name : String) : Array[Byte] = {
		val file = new File(GetProperties.Client_Export_FilePath + name)
		val reVal : Array[Byte] = new Array[Byte](file.length.intValue)
		new FileInputStream(file).read(reVal)
		reVal
	}

	def uploadHospitalDataFile(data : MultipartFormData[TemporaryFile])(implicit error_handler : Int => JsValue) : JsValue = {
		try {
			var lst : List[JsValue] = Nil
			var path = GetProperties.Manage_Upload_HospitalData_FilePath
			deleteFile(new File(path))
			data.files.foreach { x =>
				val uuid = UUID.randomUUID
				new TemporaryFile(x.ref.file).moveTo(new File(path + uuid), true)
				lst = lst :+ toJson(uuid.toString)
			}
			Json.toJson(Map("status" -> toJson("ok"), "result" -> toJson(lst)))
		} catch {
			case ex : Exception => error_handler(-1)
		}
	}

	def uploadProductMatchFile(data : MultipartFormData[TemporaryFile])(implicit error_handler : Int => JsValue) : JsValue = {
		try {
			var lst : List[JsValue] = Nil
			var path = GetProperties.Manage_Upload_ProductMatch_FilePath
			deleteFile(new File(path))
			data.files.foreach { x =>
				val uuid = UUID.randomUUID
				new TemporaryFile(x.ref.file).moveTo(new File(path + uuid), true)
				lst = lst :+ toJson(uuid.toString)
			}
			Json.toJson(Map("status" -> toJson("ok"), "result" -> toJson(lst)))
		} catch {
			case ex : Exception => error_handler(-1)
		}
	}

	def uploadMarketMatchFile(data : MultipartFormData[TemporaryFile])(implicit error_handler : Int => JsValue) : JsValue = {
		try {
			var lst : List[JsValue] = Nil
			var path = GetProperties.Manage_Upload_MarketMatch_FilePath
			deleteFile(new File(path))
			data.files.foreach { x =>
				val uuid = UUID.randomUUID
				new TemporaryFile(x.ref.file).moveTo(new File(path + uuid), true)
				lst = lst :+ toJson(uuid.toString)
			}
			Json.toJson(Map("status" -> toJson("ok"), "result" -> toJson(lst)))
		} catch {
			case ex : Exception => error_handler(-1)
		}
	}

	def uploadHospitalMatchFile(data : MultipartFormData[TemporaryFile])(implicit error_handler : Int => JsValue) : JsValue = {
		try {
			var lst : List[JsValue] = Nil
			var path = GetProperties.Manage_Upload_HospitalMatch_FilePath
			deleteFile(new File(path))
			data.files.foreach { x =>
				val uuid = UUID.randomUUID
				new TemporaryFile(x.ref.file).moveTo(new File(path + uuid), true)
				lst = lst :+ toJson(uuid.toString)
			}
			Json.toJson(Map("status" -> toJson("ok"), "result" -> toJson(lst)))
		} catch {
			case ex : Exception => error_handler(-1)
		}
	}

	def deleteFile(file : File) {
		if(file.isDirectory){
			file.listFiles().foreach(x => deleteFile(x))
		}
		file.delete()
	}
}