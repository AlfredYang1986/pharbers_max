package com.pharbers.aqll.calc.excel.exception

import org.xml.sax.SAXException

case class SAXEx(obj: java.util.List[Object], readNum: Int, readCount: Int, message: String) extends SAXException

case class NULLEx(fileName: String) extends java.lang.NullPointerException