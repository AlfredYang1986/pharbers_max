package module.jobs.search

import com.pharbers.bmmessages.CommonModules
import com.pharbers.module.MAXCallJobPusher
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

trait searchTrait {

    def callJob(func: (JsValue, String) => Map[String, AnyRef])
               (call: String)
               (jv: JsValue)
               (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val channel = cm.modules.get.get("cp").map(x => x.asInstanceOf[MAXCallJobPusher]).get
        channel.pushRecord(func(jv, call))(channel.precord)
        (Some(Map(call -> toJson("call success"))), None)
    }

    private def getAllMkt(company_id: String): JsValue = {
        toJson( "mkt1" :: "mkt2" :: Nil )
    }

    def searchAllMkt(jv: JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val company_id = (jv \ "user" \ "company" \ "company_id").asOpt[String].get
        val temp = Some(
            Map(
                "markets" -> getAllMkt(company_id)
            )
        )

        (temp, None)
    }

    def searchHistory(jv: JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val user_id = (jv \ "condition" \ "user_id").asOpt[String].get
        val market = (jv \ "condition" \ "market").asOpt[String].get
        val startTime = (jv \ "condition" \ "startTime").asOpt[String].get
        val endTime = (jv \ "condition" \ "endTime").asOpt[String].get
        val currentPage = (jv \ "condition" \ "currentPage").asOpt[String].get
        val pageSize = (jv \ "condition" \ "pageSize").asOpt[String].get

        val data1 = toJson(
            Map(
                "id" -> toJson("1"),
                "type" -> toJson("dataCenter"),
                "attributes" -> toJson(
                    Map(
                        "date" -> toJson("2018-10"),
                        "province" -> toJson("北京"),
                        "market" -> toJson("降压药"),
                        "product" -> toJson("巴拉巴拉巴拉"),
                        "sales" -> toJson("100"),
                        "units" -> toJson("20")
                    )
                )
            )
        )
        val data2 = toJson(
            Map(
                "id" -> toJson("2"),
                "type" -> toJson("dataCenter"),
                "attributes" -> toJson(
                    Map(
                        "date" -> toJson("2018-11"),
                        "province" -> toJson("天津"),
                        "market" -> toJson("退烧药"),
                        "product" -> toJson("小魔仙变身"),
                        "sales" -> toJson("100"),
                        "units" -> toJson("20")
                    )
                )
            )
        )
        val data = toJson( data1 :: data2 :: Nil)

        val temp = Some(
            Map(
                "data" -> data,
                "page" -> toJson(
                    Map(
                        "itemsCount" -> toJson("100"),
                        "pagesCount" -> toJson("10")
                    )
                )
            )
        )

        (temp, None)
    }

    def searchSimpleCheckSelect(jv: JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val job_id = (jv \ "condition" \ "job_id").asOpt[String].get
        val user_id = (jv \ "user" \ "user_id").asOpt[String].get
        val company_id = (jv \ "user" \ "company" \ "company_id").asOpt[String].get
        val temp = Some(
            Map(
                "years" -> toJson(
                    "201701" :: "201702" :: Nil
                ),
                "markets" -> getAllMkt(company_id)
            )
        )

        (temp, None)
    }

    def searchSimpleCheck(jv: JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val job_id = (jv \ "condition" \ "job_id").asOpt[String].get
//        val market = (jv \ "condition" \ "market").asOpt[String].get
        val years = (jv \ "condition" \ "years").asOpt[String].get
        val user_id = (jv \ "user" \ "user_id").asOpt[String].get
        val company_id = (jv \ "user" \ "company" \ "company_id").asOpt[String].get

        val temp = Some(
            Map(
                "hospital" -> toJson(
                    Map(
                        "baselines" -> toJson("100" :: "200" :: "300" :: "400" :: "500" :: "600" :: "700" :: "800" :: "900" :: "1000" :: "1100" :: "1200" :: Nil),
                        "samplenumbers" -> toJson("100" :: "200" :: "300" :: "400" :: "500" :: "600" :: "700" :: "800" :: "900" :: "1000" :: "1100" :: "1200" :: Nil),
                        "currentNumber" -> toJson("123"),
                        "lastYearNumber" -> toJson("234")
                    )
                ),
                "product" -> toJson(
                    Map(
                        "baselines" -> toJson("100" :: "200" :: "300" :: "400" :: "500" :: "600" :: "700" :: "800" :: "900" :: "1000" :: "1100" :: "1200" :: Nil),
                        "samplenumbers" -> toJson("100" :: "200" :: "300" :: "400" :: "500" :: "600" :: "700" :: "800" :: "900" :: "1000" :: "1100" :: "1200" :: Nil),
                        "currentNumber" -> toJson("123"),
                        "lastYearNumber" -> toJson("234")
                    )
                ),
                "sales" -> toJson(
                    Map(
                        "baselines" -> toJson("100" :: "200" :: "300" :: "400" :: "500" :: "600" :: "700" :: "800" :: "900" :: "1000" :: "1100" :: "1200" :: Nil),
                        "samplenumbers" -> toJson("100" :: "200" :: "300" :: "400" :: "500" :: "600" :: "700" :: "800" :: "900" :: "1000" :: "1100" :: "1200" :: Nil),
                        "currentNumber" -> toJson("123"),
                        "lastYearNumber" -> toJson("234")
                    )
                ),
                "notfindhospital" -> toJson(
                    Map(
                        "index" -> toJson("1"),
                        "hospitalName" -> toJson("1aaa"),
                        "province" -> toJson("1"),
                        "city" -> toJson("aaa"),
                        "cityLevel" -> toJson("5")
                    ) ::  Map(
                        "index" -> toJson("2"),
                        "hospitalName" -> toJson("bbbbb"),
                        "province" -> toJson("1"),
                        "city" -> toJson("bbbbbb"),
                        "cityLevel" -> toJson("4")
                    ) :: Map(
                        "index" -> toJson("3"),
                        "hospitalName" -> toJson("cccccc"),
                        "province" -> toJson("1"),
                        "city" -> toJson("cccccc"),
                        "cityLevel" -> toJson("3")
                    ) :: Nil
                )
            )
        )

        (temp, None)
    }

    def searchResultCheck(jv: JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val job_id = (jv \ "condition" \ "job_id").asOpt[String].get
        val market = (jv \ "condition" \ "market").asOpt[String].get
        val years = (jv \ "condition" \ "years").asOpt[String].get
        val user_id = (jv \ "user" \ "user_id").asOpt[String].get
        val company_id = (jv \ "user" \ "company" \ "company_id").asOpt[String].get

        val temp = Some(
            Map(
                "indicators" -> toJson(
                    Map(
                        "marketSumSales" -> toJson(
                            Map(
                                "currentNumber" -> toJson("123.11"),
                                "lastYearPercentage" -> toJson("3.12")
                            )
                        ),
                        "productSales" -> toJson(
                            Map(
                                "currentNumber" -> toJson("123.11"),
                                "lastYearPercentage" -> toJson("3.12")
                            )
                        )
                    )
                ),
                "trend" -> toJson(
                    Map(
                        "date" -> toJson("201701"),
                        "percentage" -> toJson("12.1"),
                        "marketSales" -> toJson("1000")
                    )
                            :: Map(
                        "date" -> toJson("201612"),
                        "percentage" -> toJson("13.1"),
                        "marketSales" -> toJson("100")
                    ) :: Nil
                ),
                "region" -> toJson(
                    Map(
                        "name" -> toJson("北京"),
                        "value" -> toJson("12111"),
                        "prodcutSales" -> toJson("12"),
                        "percentage" -> toJson("1.1")
                    ) :: Map(
                        "name" -> toJson("天津"),
                        "value" -> toJson("16311"),
                        "prodcutSales" -> toJson("12"),
                        "percentage" -> toJson("1.1")
                    ) :: Map(
                        "name" -> toJson("上海"),
                        "value" -> toJson("18311"),
                        "prodcutSales" -> toJson("12"),
                        "percentage" -> toJson("1.1")
                    ) :: Nil
                ),
                "mirror" -> toJson(
                    Map(
                        "provinces" -> toJson(
                            Map(
                                "current" -> toJson(
                                    Map(
                                        "area" -> toJson("北京市"),
                                        "marketSales" -> toJson("1111"),
                                        "percentage" -> toJson("1.1")
                                    ) :: Map(
                                        "area" -> toJson("天津市"),
                                        "marketSales" -> toJson("1111"),
                                        "percentage" -> toJson("1.1")
                                    ) :: Nil
                                ),
                                "lastyear" -> toJson(
                                    Map(
                                        "area" -> toJson("北京市"),
                                        "marketSales" -> toJson("1111"),
                                        "percentage" -> toJson("1.1")
                                    ) :: Map(
                                        "area" -> toJson("天津市"),
                                        "marketSales" -> toJson("1111"),
                                        "percentage" -> toJson("1.1")
                                    ) :: Nil
                                )
                            )
                        ),
                        "city" -> toJson(
                            Map(
                                "current" -> toJson(
                                    Map(
                                        "area" -> toJson("杭州市"),
                                        "marketSales" -> toJson("1111"),
                                        "percentage" -> toJson("1.1")
                                    ) :: Map(
                                        "area" -> toJson("天津市"),
                                        "marketSales" -> toJson("1111"),
                                        "percentage" -> toJson("1.1")
                                    ) :: Nil
                                ),
                                "lastyear" -> toJson(
                                    Map(
                                        "area" -> toJson("北京市"),
                                        "marketSales" -> toJson("1111"),
                                        "percentage" -> toJson("1.1")
                                    ) :: Map(
                                        "area" -> toJson("天津市"),
                                        "marketSales" -> toJson("1111"),
                                        "percentage" -> toJson("1.1")
                                    ) :: Nil
                                )
                            )
                        )
                    )
                )
            )
        )

        (temp, None)
    }

}
