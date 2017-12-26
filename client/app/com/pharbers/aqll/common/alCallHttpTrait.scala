package com.pharbers.aqll.common

import com.pharbers.aqll.common.alFileHandler.akkaConfig._
import com.pharbers.http.HTTP
import play.api.libs.json.JsValue

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