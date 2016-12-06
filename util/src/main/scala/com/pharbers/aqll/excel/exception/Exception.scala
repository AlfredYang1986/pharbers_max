package com.pharbers.aqll.excel.exception

import scala.Exception

import com.pharbers.aqll.util.errorcode.ErrorCode._

//java.lang.Exception
case class ReadFileException(name: String) extends Exception

case class ExcelDataException(msg: String) extends Exception
