package module.business
import com.pharbers.aqll.util.dao.from
import com.mongodb.casbah.Imports._
import com.pharbers.aqll.util.dao._data_connection_cores
import play.api.libs.json.Json
import play.api.libs.json.Json.{toJson}
import play.api.libs.json.JsValue
/**
  * Created by wli on 2017/1/4.
  */
object MarketsModule {
    def pushMarkets(token : String) : List[String] = {
        var markets = _data_connection_cores.getCollection(token).distinct("Market")
        markets.toList.asInstanceOf[List[String]]
        //("博路定市场" :: "降压药市场" :: "高血压市场" :: Nil)
    }

    def MarketJsValue(x : MongoDBObject) : JsValue = {
        toJson(Map("Market" -> toJson(x.getAs[String]("Market").get)))
    }
}
