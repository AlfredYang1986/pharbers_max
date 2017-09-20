package module.common

import scala.collection.mutable.ArrayBuffer

/**
  * Created by liwei on 2017/4/24.
  */

object alNearDecemberMonth {
    /**
      * @author liwei
      * @param date
      * @return
      */
    def diff12Month(date: String): Array[String] = {
        val year = date.substring(0, 4).toInt
        val month = date.substring(4, date.length).toInt
        val temp = new ArrayBuffer[String]()
        val lst = diffDate(year, month, (year.toInt - 1), month)(temp)
        lst.sortBy(x => x)
    }

    /**
      * @author liwei
      * @param cur_year
      * @param cur_month
      * @param ear_year
      * @param ear_month
      * @param temp
      * @return
      */
    def diffDate(cur_year: Int, cur_month: Int, ear_year: Int, ear_month: Int)(temp: ArrayBuffer[String]): Array[String] = {
        (ear_year, ear_month) match {
            case (x, y) if x.equals(cur_year) && y.equals(cur_month) => temp.toArray
            case _ => {
                ear_month match {
                    case i if i >= 12 => {
                        temp += s"$cur_year${diffMonth(s"${i + 1 - 12}")}"
                        diffDate(cur_year, cur_month, cur_year, i + 1 - 12)(temp)
                    }
                    case _ => {
                        temp += s"$ear_year${diffMonth(s"${ear_month + 1}")}"
                        diffDate(cur_year, cur_month, ear_year, ear_month + 1)(temp)
                    }
                }
            }
        }
    }

    /**
      * @author liwei
      * @param month
      * @return
      */
    def diffMonth(month: String): String = month.length match {
        case 1 => 0 + month
        case _ => month
    }
}
