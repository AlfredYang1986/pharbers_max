package com.pharbers.aqll.util.page

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

/**
  * Created by Wli on 2017/2/14 0014.
  */
object Page {

    def TAKE = 10
    def SKIP(PAGE_CURRENT : Int) : Int = ((PAGE_CURRENT-1)*TAKE)

    /**
      *
      * @param PAGE_CURRENT     当前页
      * @param TOTLE_RECORD     总记录
      * @return
      */
    def Page(PAGE_CURRENT : Int, TOTLE_RECORD : Int) : List[Map[String,JsValue]] = {
        var ROW_START = (PAGE_CURRENT-1)*TAKE+1
        var ROW_END = PAGE_CURRENT*TAKE
        if(TOTLE_RECORD == 0){
            ROW_START = 0
            ROW_END = 0
        }
        Map(
            "ROW_START" -> toJson(ROW_START),
            "ROW_END" -> toJson(ROW_END),
            "PAGE_CURRE" -> toJson(PAGE_CURRENT),
            "TOTLE_PAGE" -> toJson(if(TOTLE_RECORD%TAKE==0)(TOTLE_RECORD/TAKE)else(TOTLE_RECORD/TAKE+1)),
            "TOTLE_RECORD" -> toJson(TOTLE_RECORD),
            "SERIAL" -> toJson(SKIP(PAGE_CURRENT)+1)
        ):: Nil
    }
}
