package com.pharbers.aqll.module.fopModule

import java.io.File
import java.util.UUID
import com.pharbers.ErrorCode
import play.api.mvc.MultipartFormData
import play.api.libs.json.Json.toJson
import play.api.libs.Files.TemporaryFile
import play.api.libs.json.{JsValue, Json}
import com.pharbers.aqll.common.alFileHandler.fileConfig._

object Upload {
    def uploadFile(data: MultipartFormData[TemporaryFile]): JsValue = {
        try {
            var lst: List[JsValue] = Nil
            data.files.foreach { x =>
                val uuid = UUID.randomUUID
                val company = data.dataParts("company").headOption.map(x => x).getOrElse("")
                val path = s"$memorySplitFile$fileBase$company/Client"
                val file = new File(path)
                if (!file.exists()) file.mkdir()
                new TemporaryFile(x.ref.file).moveTo(new File(s"$path/$uuid"), true)
                lst = lst :+ toJson(uuid.toString)
            }
            Json.toJson(Map("status" -> toJson("ok"), "result" -> toJson(lst)))
        } catch {
            case _: Exception => ErrorCode.errorToJson("upload error")
        }
    }
}
