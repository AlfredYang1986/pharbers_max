package com.pharbers.aqll.common

import com.pharbers.aqll.common.alErrorCode.{alClient, alServer}

/**
  * Created by liwei on 2017/5/15.
  */
object ClientErrorCode {
  def apply: alClient = new alClient()
}

object ServerErrorCode {
  def apply: alServer = new alServer()
}
