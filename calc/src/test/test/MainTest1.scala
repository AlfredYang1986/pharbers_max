package com.pharbers.aqll.calc.main

import com.pharbers.aqll.calc.excel.core._

object MainTest1 extends App{
    
    val hospdata_ch_file = "config/admin/HospDataStruct.xml"
    val hospdata_en_file = "config/admin/FieldNamesHospDataStruct.xml"
    val hospdata_file = """E:\文件\法伯相关\MAX改建\程序测试数据\管理员上传\8000家taxol医院数据库表.xlsx"""
    
    val hospdatabase = hospdatainteractparser(hospdata_en_file, hospdata_ch_file)
    hospdatabase.startParse(hospdata_file, 1)
    println(hospdatabase.resultlist.size)
}