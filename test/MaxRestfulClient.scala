import javax.inject.Inject
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import play.api.libs.ws.WSClient

import scala.concurrent.{Await, ExecutionContext, Future}

/**
  * Created by alfredyang on 07/07/2017.
  * 每一个max restful post 接口都需要写单元测试
  */
class MaxRestfulClient(ws: WSClient, baseUrl: String)(implicit ec: ExecutionContext) {
    @Inject def this(ws: WSClient, ec: ExecutionContext) = this(ws, "http://127.0.0.1:9000")(ec)

    def authLoginWithPhone(phone : String, screen_name : String = "", screen_photo : String = "") : Future[JsValue] = {
        ws.url(baseUrl + "/al/auth")
            .withHeaders("Accept" -> "application/json", "Content-Type" -> "application/json")
            .post(toJson(Map("phone" -> phone, "screen_name" -> screen_name, "screen_photo" -> screen_photo)))
            .map { response =>
//                println(response.json)
                response.json
            }
    }
}
