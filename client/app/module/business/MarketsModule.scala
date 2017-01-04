package module.business
import com.pharbers.aqll.util.dao._data_connection_cores
/**
  * Created by wli on 2017/1/4.
  */
object MarketsModule {
    def pushMarkets(token : String) : List[String] = {
        var markets = _data_connection_cores.getCollection(token).distinct("Market")
        markets.toList.asInstanceOf[List[String]]
    }
}
