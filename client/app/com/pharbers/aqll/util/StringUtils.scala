package com.pharbers.aqll.util

import java.security.MessageDigest

object StringUtils {

  def removeSpace(str: String): String = str.replaceAll("\\s", "")

  def md5(str: String): String = {
    val hash = MessageDigest.getInstance("MD5").digest(str.getBytes)
    hash.map("%02x".format(_)).mkString
  }
}