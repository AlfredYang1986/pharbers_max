package module.common

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

/**
  * Created by liwei on 2017/5/4.
  */
object alMessage {

  def Message0 = "未知" :: "unknown" :: Nil
  def Message1 = "操作成功" :: "success" :: Nil
  def Message2 = "操作失败" :: "fail" :: Nil
  def Message3 = "目标已存在" :: "exist" :: Nil
  def Message4 = "目标不存在" :: "nonexist" :: Nil

  def getMessage(args: Int): JsValue ={
    args match {
      case 1 => toJson(Map("result" -> toJson(Message1.head),"status" -> toJson(Message1.tail.head)))
      case 2 => toJson(Map("result" -> toJson(Message2.head),"status" -> toJson(Message2.tail.head)))
      case 3 => toJson(Map("result" -> toJson(Message3.head),"status" -> toJson(Message3.tail.head)))
      case 4 => toJson(Map("result" -> toJson(Message4.head),"status" -> toJson(Message4.tail.head)))
      case _ => toJson(Map("result" -> toJson(Message0.head),"status" -> toJson(Message0.tail.head)))
    }
  }
}
