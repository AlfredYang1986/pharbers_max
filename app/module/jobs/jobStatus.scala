package module.jobs

/**
  * Created by spark on 18-4-20.
  */
sealed class jobStatusDefine(val id : Int, val des : String)

object jobStatus {
    case class jobCreated() extends jobStatusDefine(0, "任务创建完成")
    case class jobPanel() extends jobStatusDefine(1, "正在生成样本数据")
    case class jobPaneled() extends jobStatusDefine(2, "样本数据生成完成")
    case class jobCalc() extends jobStatusDefine(3, "正在进行Max计算")
    case class jobCalced() extends jobStatusDefine(4, "Max计算完成")
    case class jobDone() extends jobStatusDefine(9, "任务执行完成")
    case class jobKill() extends jobStatusDefine(-1, "任务强制终止")
}