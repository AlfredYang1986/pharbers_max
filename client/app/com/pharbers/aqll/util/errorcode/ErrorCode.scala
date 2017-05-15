package com.pharbers.aqll.util.errorcode

import play.api.libs.json.Json
import play.api.libs.json.Json._
import play.api.libs.json.JsValue
/**
  * Created by liwei on 2017/5/12.
  */

sealed class ReturnValDefines(var s : Int,var m: String)

object ReturnVal {
  case object success extends ReturnValDefines(0, "success")
  case object failed extends ReturnValDefines(-1, "failed")
}

object ErrorCode {

  case class ErrorNode(name: String, code: Int, message: String,status : String)

  private def xls: List[ErrorNode] = List(

    // info -1 to -99
    new ErrorNode("info input company name", -1, "请输入公司名称",ReturnVal.failed.m),
    new ErrorNode("info input user name", -2, "请输入用户名称",ReturnVal.failed.m),
    new ErrorNode("info input account", -3, "请输入账号",ReturnVal.failed.m),
    new ErrorNode("info input password", -4, "请输入密码",ReturnVal.failed.m),
    new ErrorNode("info input phone", -5, "请输入手机号码",ReturnVal.failed.m),
    new ErrorNode("info input company email", -6, "请输入公司联系邮箱",ReturnVal.failed.m),
    new ErrorNode("info input company address", -7, "请输入公司注册地址",ReturnVal.failed.m),
    new ErrorNode("info input market name", -8, "请输入市场名称",ReturnVal.failed.m),

    // warn -100 to -199
    new ErrorNode("warn input", -100, "输入参数有错误",ReturnVal.failed.m),
    new ErrorNode("warn email", -101, "输入正确的邮件地址",ReturnVal.failed.m),
    new ErrorNode("warn cell phone", -102, "输入正确的手机号",ReturnVal.failed.m),
    new ErrorNode("warn code", -103, "输入正确的验证码",ReturnVal.failed.m),
    new ErrorNode("warn user not exist", -104, "用户不存在或者密码不正确",ReturnVal.failed.m),
    new ErrorNode("warn email", -105, "输入正确的邮件地址",ReturnVal.failed.m),
    new ErrorNode("info check the delete item", -120, "请勾选要删除的条目",ReturnVal.failed.m),
    new ErrorNode("info delete success", -121, "删除成功",ReturnVal.success.m),
    new ErrorNode("info delete failed", -122, "删除失败",ReturnVal.failed.m),
    new ErrorNode("info add success", -123, "添加成功",ReturnVal.success.m),
    new ErrorNode("info add failed", -124, "添加失败",ReturnVal.failed.m),
    new ErrorNode("info edit success", -125, "修改成功",ReturnVal.success.m),
    new ErrorNode("info edit failed", -126, "修改失败",ReturnVal.failed.m),
    new ErrorNode("info file export success", -127, "文件导出成功",ReturnVal.success.m),
    new ErrorNode("info file export failed", -128, "文件导出失败",ReturnVal.failed.m),
    new ErrorNode("info target exists", -129, "该目标已存在",ReturnVal.failed.m),
    new ErrorNode("info target does not exists", -130, "该目标不存在",ReturnVal.failed.m),

    // error -200 to -299
    new ErrorNode("error input", -200, "你输入的参数不正确",ReturnVal.failed.m),

    new ErrorNode("not implement", -998, "工程师正在玩命的开发中",ReturnVal.failed.m),
    new ErrorNode("unknown error", -999, "unknown error",ReturnVal.failed.m)
  )

  def getErrorCodeByName(name: String): Int = (xls.find(x => x.name == name)) match {
    case Some(y) => y.code
    case None => -9999
  }

  def getErrorMessageByName(name: String): String = (xls.find(x => x.name == name)) match {
    case Some(y) => y.message
    case None => "unknow error"
  }

  def getErrorMessageByCode(code : Int) : String = (xls.find(x => x.code == code)) match {
    case Some(y) => y.message
    case None => "unknow error"
  }

  def getErrorStatusByName(name: String): String = (xls.find(x => x.name == name)) match {
    case Some(y) => y.status
    case None => ReturnVal.failed.m
  }

  def getErrorStatusByCode(code : Int): String = (xls.find(x => x.code == code)) match {
    case Some(y) => y.status
    case None => ReturnVal.failed.m
  }

  def errorMessageByCode(code : Int) : (Int, String) = (code, getErrorMessageByCode(code))

  def errorToJson(name : String) : JsValue =
    Json.toJson(Map("status" -> toJson(this.getErrorStatusByName(name)), "error" ->
      toJson(Map("code" -> toJson(this.getErrorCodeByName(name)), "message" -> toJson(this.getErrorMessageByName(name))))))

  def errorToJsonByCode(code : Int) : JsValue =
    Json.toJson(Map("status" -> toJson(this.getErrorStatusByCode(code)), "error" ->
      toJson(Map("code" -> toJson(code), "message" -> toJson(this.getErrorMessageByCode(code))))))

}