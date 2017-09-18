package bminjection.token

import play.api.libs.json.JsValue

/**
  * Created by alfredyang on 01/06/2017.
  */
trait AuthTokenTrait {
    def encrypt2Token(js : JsValue) : String
    def decrypt2JsValue(auth_token : String) : JsValue
}
