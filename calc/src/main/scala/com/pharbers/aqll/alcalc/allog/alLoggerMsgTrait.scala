package com.pharbers.aqll.alcalc.allog

import org.slf4j.LoggerFactory

/**
  * Created by qianpeng on 2017/4/11.
  */
trait alLoggerMsgTrait {
	def logger = LoggerFactory.getLogger(getClass)
}


