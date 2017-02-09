package module.business

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
/**
  * Created by Wli on 2017/2/8 0008.
  */
object GroupByProvinceFunc {
    def apply(results : List[Map[String,JsValue]])(lst: List[Map[String,JsValue]]): List[Map[String, JsValue]] ={
        ((results ::: lst).groupBy{ x =>
            (x.get("Year").get,x.get("Month").get,x.get("Region_Name").get,x.get("Province_Name").get,x.get("ProductMinunt").get,x.get("Market").get)
        }.map{ y =>
            val Salessum = y._2.map(z => z.get("Sales").get.as[Double]).sum
            val Unitssum = y._2.map(x => x.get("Units").get.as[Double]).sum
            Map(
                "Year" -> toJson(y._1._1),
                "Month" ->toJson(y._1._2),
                "Region_Name" -> toJson(y._1._3),
                "Province_Name" -> toJson(y._1._4),
                "ProductMinunt" -> toJson(y._1._5),
                "Market" -> toJson(y._1._6),
                "Sales" -> toJson(Salessum),
                "Units" -> toJson(Unitssum)
            )
        }) toList
    }
}

object GroupByCityFunc {
    def apply(results : List[Map[String,JsValue]])(lst: List[Map[String,JsValue]]): List[Map[String, JsValue]] = {
        ((results ::: lst).groupBy{ x =>
            (x.get("Year").get,x.get("Month").get,x.get("Region_Name").get,x.get("Province_Name").get,x.get("City_Name").get,x.get("City_Level").get,x.get("ProductMinunt").get,x.get("Market").get)
        }.map{ y =>
            val Salessum = y._2.map(z => z.get("Sales").get.as[Double]).sum
            val Unitssum = y._2.map(x => x.get("Units").get.as[Double]).sum
            Map(
                "Year" -> toJson(y._1._1),
                "Month" ->toJson(y._1._2),
                "Region_Name" -> toJson(y._1._3),
                "Province_Name" -> toJson(y._1._4),
                "City_Name" -> toJson(y._1._5),
                "City_Level" -> toJson(y._1._6),
                "ProductMinunt" -> toJson(y._1._7),
                "Market" -> toJson(y._1._8),
                "Sales" -> toJson(Salessum),
                "Units" -> toJson(Unitssum)
            )
        }) toList
    }
}

object GroupByHospitalFunc {
    def apply(results : List[Map[String,JsValue]])(lst: List[Map[String,JsValue]]): List[Map[String, JsValue]] = {
        ((results ::: lst).groupBy{ x =>
            (x.get("Year").get,x.get("Month").get,x.get("Region_Name").get,x.get("Province_Name").get,x.get("City_Name").get,x.get("City_Level").get,x.get("Hosp_Name").get,x.get("Hosp_Level").get,x.get("ProductMinunt").get,x.get("Market").get)
        }.map{ y =>
            val Salessum = y._2.map(z => z.get("Sales").get.as[Double]).sum
            val Unitssum = y._2.map(x => x.get("Units").get.as[Double]).sum
            Map(
                "Year" -> toJson(y._1._1),
                "Month" ->toJson(y._1._2),
                "Region_Name" -> toJson(y._1._3),
                "Province_Name" -> toJson(y._1._4),
                "City_Name" -> toJson(y._1._5),
                "City_Level" -> toJson(y._1._6),
                "Hosp_Name" -> toJson(y._1._7),
                "Hosp_Level" -> toJson(y._1._8),
                "ProductMinunt" -> toJson(y._1._9),
                "Market" -> toJson(y._1._10),
                "Sales" -> toJson(Salessum),
                "Units" -> toJson(Unitssum)
            )
        }) toList
    }
}
