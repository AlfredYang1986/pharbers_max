package com.pharbers.aqll.module.fopModule

import java.io.File
import java.util.UUID

import com.pharbers.ErrorCode
import com.pharbers.panel.panel_path_obj
import play.api.mvc.MultipartFormData
import play.api.libs.json.Json.toJson
import play.api.libs.Files.TemporaryFile
import play.api.libs.json.{JsValue, Json}

object Upload {
    def uploadFile(data: MultipartFormData[TemporaryFile]): JsValue = {
        try {
            var lst: List[JsValue] = Nil
            data.files.foreach { x =>
                val uuid = UUID.randomUUID
                val company = data.dataParts("company").headOption.map(x => x).getOrElse("")
                val path = panel_path_obj.p_client_path
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
