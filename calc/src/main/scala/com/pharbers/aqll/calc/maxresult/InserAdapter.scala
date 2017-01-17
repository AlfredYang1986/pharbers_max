package com.pharbers.aqll.calc.maxresult

import com.pharbers.aqll.calc.util.MD5

object InserAdapter {
    def apply(filepath: String, company: String, time: Long) = (filepath, company, MD5.md5(filepath+company+time.toString()), time)
}