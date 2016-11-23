package com.pharbers.aqll.calc.excel.helpFunc

object StringOption {
  /***
   * 截取全部空格
   */
  def takeStringSpace(str: String): String = str.replaceAll("\\s", "")
}