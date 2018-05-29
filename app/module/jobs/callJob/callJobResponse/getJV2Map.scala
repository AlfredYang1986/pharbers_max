package module.jobs.callJob.callJobResponse

import play.api.libs.json.JsValue

trait getJV2Map {
    def getArgs2Map(jv: JsValue): Map[String, String] = {
        (jv \ "result").asOpt[String].get
                .tail.init
                .split(",").map(_.split("="))
                .map(x => x.head.trim -> x.last.trim)
                .toMap
    }
}
