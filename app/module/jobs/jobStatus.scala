package module.jobs

import play.api.libs.json.JsValue

/**
  * Created by spark on 18-4-20.
  */
sealed class jobStatusDefine(val id : Int, val des : String)

object jobStatus {
    case class jobCreated(jv: JsValue) extends jobStatusDefine(0, "任务创建完成")
    case class jobPaneling(jv: JsValue) extends jobStatusDefine(1, "正在生成样本数据")
    case class jobPaneled(jv: JsValue) extends jobStatusDefine(2, "样本数据生成完成")
    case class jobCalcing(jv: JsValue) extends jobStatusDefine(3, "正在进行Max计算")
    case class jobCalced(jv: JsValue) extends jobStatusDefine(4, "Max计算完成")
    case class jobDoned(jv: JsValue) extends jobStatusDefine(9, "任务执行完成")
    case class jobKilled(jv: JsValue) extends jobStatusDefine(-1, "任务强制终止")
}