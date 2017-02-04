package com.pharbers.aqll.util

import java.security.MessageDigest

object MD5 {
  def md5(str: String): String = {
    val hash = MessageDigest.getInstance("MD5").digest(str.getBytes)
    hash.map("%02x".format(_)).mkString
  }
//  java.security.MessageDigest.getInstance("MD5").digest(text.getBytes()).map(0xFF & _).map { "%02x".format(_) }.foldLeft(""){_ + _}
}