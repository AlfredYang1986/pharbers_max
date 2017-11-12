package com.pharbers.aqll.alCalcOther.alMessgae

import com.pharbers.http.HTTP
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

import scala.collection.immutable.Map

/**
  * Created by clock on 17-11-12.
  */
case class alWebSocket(uid: String) {
    val ws = HTTP("http://127.0.0.1:9000/akka/callback").header("Accept" -> "application/json", "Content-Type" -> "application/json")

    def post(msg: Map[String, String]): JsValue = {
        val json = toJson(
            Map(
                "condition" -> Map(
                    "uid" -> toJson(uid),
                    "msg" -> toJson(msg))
            )
        )
        ws.post(json)
    }
}
