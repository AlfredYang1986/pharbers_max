package com.pharbers.aqll.common.alCmd.pycmd

case class PyConfig(pyDir: String,
                    pyFileName: String,
                    company: Option[String] = None,
                    yearAndMonth: Option[String] = None) {
    def toArgs: String = s"$pyDir$pyFileName ${(if(company.isEmpty) "" else company.get)} ${(if(yearAndMonth.isEmpty) "" else yearAndMonth.get)}"
}