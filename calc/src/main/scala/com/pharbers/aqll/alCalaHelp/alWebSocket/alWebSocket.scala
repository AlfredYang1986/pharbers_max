package com.pharbers.aqll.alCalaHelp.alWebSocket

import com.pharbers.http.HTTP
import play.api.libs.json.Json.toJson
import com.pharbers.aqll.alCalaHelp.alLog.alTempLog
import scala.collection.immutable.Map
import com.pharbers.baseModules.PharbersInjectModule

/**
  * Created by clock on 17-11-12.
  */
case class alWebSocket(uid: String) extends PharbersInjectModule {
    override val id: String = "wsocket-content"
    override val configPath: String = "pharbers_config/wsocket_content.xml"
    override val md = "remote_connect" :: "local_connect" :: "url" :: Nil

    val local_connect: String = config.mc.find(p => p._1 == "local_connect").get._2.toString
    val url: String = config.mc.find(p => p._1 == "url").get._2.toString

    lazy val ws = HTTP(s"http://$local_connect$url")
            .header("Accept" -> "application/json", "Content-Type" -> "application/json")

    def post(msg: Map[String, String]) = {
        try{
            val json = toJson(
                Map(
                    "condition" -> Map(
                        "uid" -> toJson(uid),
                        "msg" -> toJson(msg))
                )
            )
            ws.post(json)
        } catch {
            case _: java.net.ConnectException => alTempLog("web socket ConnectException, failed msg = " + msg)
        }
    }
}
