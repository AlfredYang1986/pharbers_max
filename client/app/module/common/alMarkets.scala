package module.common

import com.pharbers.aqll.common.alDao.{_data_connection_basic, _data_connection_cores}
/**
  * Created by liwei on 2017/1/4.
  */
object alMarkets {
    /**
      * @author liwei
      * @param str
      * @return
      */
    def alGetMarkets(str: String): List[String] =  str match {
        case "sc"   =>  _data_connection_cores.getCollection("FactResult").find().toList.groupBy(x => x.get("Market")).toList.map(y => y._1.asInstanceOf[String])
        case _  =>  _data_connection_basic.getCollection("Market").find().toList.map(x => x.get("Market_Name").asInstanceOf[String])
    }
}
