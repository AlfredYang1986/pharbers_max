package com.pharbers.aqll.common

import com.pharbers.http.HTTP
import play.api.libs.json.JsValue
import com.pharbers.common.another_file_package.akkaConfig._

/**
  * Created by liwei on 2017/5/17.
  */
trait alCallHttpTrait {

    val url: String

    val data: JsValue

    def call: JsValue = HTTP(url).header("Accept" -> "application/json", "Content-Type" -> "application/json").post(data).as[JsValue]
}

case class alCallHttp(businessType: String, data: JsValue) extends alCallHttpTrait {

    override val url: String = s"$akkaIp:$akkaPort$businessType"
}