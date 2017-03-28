package module.common

import com.pharbers.aqll.util.dao._data_connection_basic

import scala.collection.mutable.ListBuffer
/**
  * Created by Wli on 2017/1/4.
  */
object MarketsModule {
    def pushMarkets = {
        var markets = _data_connection_basic.getCollection("Market").find()
        var marketlst : ListBuffer[String] = new ListBuffer[String]()
        while(markets.hasNext){
            marketlst.append(markets.next().get("Market_Name").asInstanceOf[String])
        }
        marketlst.toList
    }
}
