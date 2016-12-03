package com.pharbers.aqll.calc.maxresult

import com.pharbers.aqll.calc.util.MD5

object InserAdapter {
    def apply(fileName: String, company: String, time: Long) = (fileName, company, MD5.md5(fileName+company+time.toString()), time)
}