package module.jobs.callJob

/**
  * Created by spark on 18-4-20.
  */
sealed class jobStatusDefine(val id : Int, val des : String)

object jobStatus {
    case class jobCreated() extends jobStatusDefine(0, "任务创建完成")
    case class jobYmCalc() extends jobStatusDefine(1, "正在检查源数据中的月份")
    case class jobYmCalced() extends jobStatusDefine(2, "检查源数据月份完成")
    case class jobPanel() extends jobStatusDefine(3, "正在生成样本数据")
    case class jobPaneled() extends jobStatusDefine(4, "样本数据生成完成")
    case class jobCalc() extends jobStatusDefine(5, "正在进行Max计算")
    case class jobCalced() extends jobStatusDefine(6, "Max计算完成")
    case class jobRestore() extends jobStatusDefine(7, "Max正在保存数据")
    case class jobRestored() extends jobStatusDefine(8, "Max数据保存完成")
    case class jobDone() extends jobStatusDefine(9, "任务执行完成")
    case class jobKill() extends jobStatusDefine(-1, "任务强制终止")
}