package module.common

import com.pharbers.aqll.common.alDao.data_connection
/**
  * Created by liwei on 2017/1/4.
  */
object alMarkets {
    /**
      * @author liwei
      * @param str
      * @return
      */
    def alGetMarkets(str: String,basic: data_connection,cores: data_connection): List[String] = {
        try {
            str match {
                case "sc"   =>  cores.getCollection("FactResult").find().toList.groupBy(x => x.get("Market")).toList.map(y => y._1.asInstanceOf[String])
                case _  =>  basic.getCollection("Market").find().toList.map(x => x.get("Market_Name").asInstanceOf[String])
            }
        } catch {
            case ex: Exception => Nil
        }
    }
}
