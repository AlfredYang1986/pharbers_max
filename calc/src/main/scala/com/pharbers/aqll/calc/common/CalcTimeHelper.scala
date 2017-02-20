package com.pharbers.aqll.calc.common

class CalcTimeHelper(var begin : Long) {
    def start = begin = System.currentTimeMillis()
    def lastTimes = System.currentTimeMillis() - begin
}
