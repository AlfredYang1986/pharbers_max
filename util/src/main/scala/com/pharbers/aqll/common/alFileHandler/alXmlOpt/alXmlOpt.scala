package com.pharbers.aqll.common.alFileHandler.alXmlOpt

/**
  * Created by qianpeng on 2017/5/11.
  */
object alXmlOpt {
	def apply(path: String): alXmlOpt = new alXmlOpt(path)
}

class alXmlOpt(path: String) {

	def xmlFindV(key: String) = {
		(xml.XML.loadFile(path) \ key).map(_.text)
	}
}
