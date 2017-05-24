package module.common

/**
  * Created by liwei on 2017/5/24.
  */
object alModularEnum extends Enumeration {
  type alModularEnum = Value
  // 文件上传
  val FU = Value(0,"fileupload")
  // 样本检查
  val SC = Value(1,"samplecheck")
  // 样本报告
  val SR = Value(2,"samplereport")
  // 结果检查
  val RC = Value(3,"resultcheck")
  // 结果查询
  val RQ = Value(4,"resultquery")
}
