package module.common

import com.pharbers.aqll.common.HTTP
import play.api.libs.json.JsValue
import com.pharbers.aqll.common.alFileHandler.akkaConfig._

/**
  * Created by liwei on 2017/5/17.
  */
trait alCallHttpTrait {

  val url : String

  val data : JsValue

  def call : JsValue = {
    (HTTP(url)).post(data).as[JsValue]
    //{"result":{"status":"success","message":"201611#"}}
  }
}

case class alCallHttp(businessType : String,data : JsValue) extends alCallHttpTrait {

  override val url: String = s"${akkaIp}:${akkaPort}${businessType}"
}