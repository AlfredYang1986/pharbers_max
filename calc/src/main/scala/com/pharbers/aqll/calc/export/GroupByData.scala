package com.pharbers.aqll.calc.export

import scala.collection.immutable.List

/**
  * Created by Wli on 2017/3/9 0009.
  */
object GroupByData {
    /*GroupBy Province Data*/
    def GroupByProvinceFunc(results : List[Map[String, Any]])(lst: List[Map[String, Any]]): List[Map[String, Any]] ={
        ((results ::: lst).groupBy{ x =>
            (x.get("Date").get,x.get("Provice").get,x.get("Market").get,x.get("Product").get)
        }.map { y =>
            val f_sales_sum = y._2.map(z => z.get("f_sales").get.toString.toDouble).sum
            val f_units_sum = y._2.map(x => x.get("f_units").get.toString.toDouble).sum
            Map("Date" -> y._1._1,"Provice" -> y._1._2,"Market" -> y._1._3,"Product" -> y._1._4,"f_sales" -> f_sales_sum,"f_units" -> f_units_sum)
        }).toList
    }

    /*GroupBy City Data*/
    def GroupByCityFunc(results : List[Map[String, Any]])(lst: List[Map[String, Any]]): List[Map[String, Any]] ={
        ((results ::: lst).groupBy{ x =>
            (x.get("Date").get,x.get("Provice").get,x.get("City"),x.get("Market").get,x.get("Product").get)
        }.map { y =>
            val f_sales_sum = y._2.map(z => z.get("f_sales").get.toString.toDouble).sum
            val f_units_sum = y._2.map(x => x.get("f_units").get.toString.toDouble).sum
            Map("Date" -> y._1._1,"Provice" -> y._1._2,"City" -> y._1._3,"Market" -> y._1._4,"Product" -> y._1._5,"f_sales" -> f_sales_sum,"f_units" -> f_units_sum)
        }).toList
    }

    /*GroupBy Hospital Data*/
    def GroupByHospitalFunc(results : List[Map[String, Any]])(lst: List[Map[String, Any]]): List[Map[String, Any]] ={
        ((results ::: lst).groupBy{ x =>
            (x.get("Date").get,x.get("Provice").get,x.get("City"),x.get("Panel_ID").get,x.get("Market").get,x.get("Product").get)
        }.map { y =>
            val f_sales_sum = y._2.map(z => z.get("f_sales").get.toString.toDouble).sum
            val f_units_sum = y._2.map(x => x.get("f_units").get.toString.toDouble).sum
            Map("Date" -> y._1._1,"Provice" -> y._1._2,"City" -> y._1._3,"Market" -> y._1._4,"Panel_ID" -> y._1._5,"Product" -> y._1._6,"f_sales" -> f_sales_sum,"f_units" -> f_units_sum)
        }).toList
    }
}
