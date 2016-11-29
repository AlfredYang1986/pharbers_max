package com.pharbers.aqll.calc.maxmessages

import com.pharbers.aqll.calc.split.JobDefines

trait MaxMessageTrait

abstract class CommonMessage extends MaxMessageTrait

case class excelJobStart(filename : String, cat : JobDefines)
case class excelJobEnd(filename : String)

case class startReadExcel(filename : String, cat : JobDefines)

case class end()

case class error()