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
				new TemporaryFile(x.ref.file).moveTo(new File(GetProperties.loadProperties("File.properties").getProperty("Upload_File_Path") + uuid), true)
      	  	  	//Files.moveFile(x.ref.file, new File("D:\\SourceData/Client/" + uuid), true, true)
      	  	  	lst = lst :+ toJson(uuid.toString)
      	  	}
      	    Json.toJson(Map("status" -> toJson("ok"), "result" -> toJson(lst)))
  	    } catch {
  	    	case ex : Exception => error_handler(-1)
  	    }
	}

	def downloadFile(name : String) : Array[Byte] = {
	  	val file = new File(GetProperties.loadProperties("File.properties").getProperty("File_Template_Parh") + name)
			val reVal : Array[Byte] = new Array[Byte](file.length.intValue)
			new FileInputStream(file).read(reVal)
			reVal
	}

	def uploadHospitalDataFile(data : MultipartFormData[TemporaryFile])(implicit error_handler : Int => JsValue) : JsValue = {
		try {
			var lst : List[JsValue] = Nil
			deleteFile(new File("D:\\SourceData/Manage/医院数据/"))
			data.files.foreach { x =>
				val uuid = UUID.randomUUID
				new TemporaryFile(x.ref.file).moveTo(new File("D:\\SourceData/Manage/医院数据/" + uuid), true)
				//Files.moveFile(x.ref.file, new File("D:\\SourceData/Manage/医院数据/" + uuid), true, true)
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
			deleteFile(new File("D:\\SourceData/Manage/产品匹配/"))
			data.files.foreach { x =>
				val uuid = UUID.randomUUID
				new TemporaryFile(x.ref.file).moveTo(new File("D:\\SourceData/Manage/产品匹配/" + uuid), true)
				//Files.moveFile(x.ref.file, new File("D:\\SourceData/Manage/产品匹配/" + uuid), true, true)
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
			deleteFile(new File("D:\\SourceData/Manage/市场匹配/"))
			data.files.foreach { x =>
				val uuid = UUID.randomUUID
				new TemporaryFile(x.ref.file).moveTo(new File("D:\\SourceData/Manage/市场匹配/" + uuid), true)
				//Files.moveFile(x.ref.file, new File("D:\\SourceData/Manage/市场匹配/" + uuid), true, true)
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
			deleteFile(new File("D:\\SourceData/Manage/医院匹配/"))
			data.files.foreach { x =>
				val uuid = UUID.randomUUID
				new TemporaryFile(x.ref.file).moveTo(new File("D:\\SourceData/Manage/医院匹配/" + uuid), true)
				//Files.moveFile(x.ref.file, new File("D:\\SourceData/Manage/医院匹配/" + uuid), true, true)
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