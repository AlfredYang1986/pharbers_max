package module.common

import com.pharbers.aqll.common.HTTP
import play.api.libs.json.JsValue
import com.pharbers.aqll.common.akkaConfig._

/**
  * Created by liwei on 2017/5/17.
  */
trait alCallHttpTrait {

  val url : String

  val data : JsValue

  def call : JsValue = {
    (HTTP(url)).post(data).as[JsValue]
  }
}

case class alCallHttp(businessType : String,data : JsValue) extends alCallHttpTrait {

  override val url: String = s"${Akka_Http_IP}:${Akka_Http_Port}${businessType}"
}