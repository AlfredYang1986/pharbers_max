package com.pharbers.aqll.old.client.util

object StringOption {
  /***
   * 截取全部空格
   */
  def takeStringSpace(str: String): String = str.replaceAll("\\s", "")
}