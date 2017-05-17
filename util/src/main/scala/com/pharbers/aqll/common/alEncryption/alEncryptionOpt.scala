package com.pharbers.aqll.common.alEncryption

import java.security.MessageDigest

/**
  * Created by liwei on 2017/5/15.
  */
object alEncryptionOpt {

  def md5(str: String): String = MessageDigest.getInstance("MD5").digest(str.getBytes).map("%02x".format(_)).mkString
}
