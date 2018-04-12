package module.common

import module.common.alNearDecemberMonth._

/**
  * Created by liwei on 2017/5/26.
  */
object alMatchDateByDate {
    def matchDataByDate(arr: List[Map[String, Any]], tuple_lst: (Option[List[Map[String, Any]]], Option[List[Map[String, Any]]])): List[Map[String, Any]] = {
        val date_lst = arr.map(x => Map("Date" -> x, "f_sales" -> 0.0)).toList
        tuple_lst._1 match {
            case None => {
                tuple_lst._2 match {
                    case None => date_lst
                    case Some(o) => getTuple_Date_Lst(date_lst, o)
                }
            }
            case Some(x) => {
                tuple_lst._2 match {
                    case None => getTuple_Date_Lst(date_lst, x)
                    case Some(o) => getTuple_Date_Lst(getTuple_Date_Lst(date_lst, o), getTuple_Date_Lst(date_lst, x))
                }
            }
        }
    }

    def getTuple_Date_Lst(f_lst: List[Map[String, Any]], s_lst: List[Map[String, Any]]): List[Map[String, Any]] = f_lst.map(x =>
        s_lst.find(y => y.get("Date").get.equals(x.get("Date").get)) match {
            case None => x
            case Some(o) => o
        }
    )

    def sampleCheckDateMap(date: String): List[Map[String, Any]] = {
        diff12Month(date).map(x => Map("Date" -> x, "HospNum" -> 0, "ProductNum" -> 0, "MarketNum" -> 0, "Sales" -> 0.0, "Units" -> 0.0)).toList
    }

    def resultCheckDateMap(date: String): List[Map[String, Any]] = {
        diff12Month(date).map(x => Map("Date" -> x, "f_sales" -> 0.0)).toList
    }
}
