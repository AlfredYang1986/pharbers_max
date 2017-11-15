package com.pharbers.aqll.module.fopModule

import java.io.File
import java.util.UUID

import com.pharbers.ErrorCode
import com.pharbers.aqll.common.alFileHandler.fileConfig._
import play.api.libs.Files.TemporaryFile
import play.api.libs.json.{JsValue, Json}
import play.api.libs.json.Json.toJson
import play.api.mvc.MultipartFormData

object Upload {
    def uploadFile(data: MultipartFormData[TemporaryFile]): JsValue = {
        try {
            var lst: List[JsValue] = Nil
            data.files.foreach { x =>
                val uuid = UUID.randomUUID
//                val company = data.dataParts("company").headOption.map(x => x).getOrElse("")
//                val path = s"$fileBase$company$outPut"
                val path = s"../calc/config/FileBase/fea9f203d4f593a96f0d6faa91ba24ba/Client"
                val file = new File(path)
                if (!file.exists()) {
                    println(file.exists())
                    file.mkdir()
                }
                new TemporaryFile(x.ref.file).moveTo(new File(s"$path/$uuid"), true)
                lst = lst :+ toJson(uuid.toString)
            }
            Json.toJson(Map("status" -> toJson("ok"), "result" -> toJson(lst)))
        } catch {
            case ex: Exception =>
                println(ex)
                ErrorCode.errorToJson("upload error")
        }
    }
}
