package module.common

import com.pharbers.aqll.common.alDao.{_data_connection_basic, _data_connection_cores}
/**
  * Created by Wli on 2017/1/4.
  */
object alMarkets {
    /**
      * @author liwei
      * @param str
      * @return
      */
    def alGetMarkets(str: String): List[String] = {
        try {
            str match {
                case "sc" => alSampleCheckMarkets
                case _ => alOtherMarkets
            }
        } catch {
            case e: Exception => {
                println(s"$str 获取市场信息失败！${e.getMessage}")
                Nil
            }
        }
    }

    def alSampleCheckMarkets = _data_connection_cores.getCollection("FactResult").find().toList.groupBy(x => x.get("Market")).toList.map(y => y._1.asInstanceOf[String])

    def alOtherMarkets = _data_connection_basic.getCollection("Market").find().toList.map(x => x.get("Market_Name").asInstanceOf[String])

}
