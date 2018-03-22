package com.pharbers.aqll.alStart.alEntry

import com.pharbers.aqll.alCalcHelp.alFinalDataProcess.alCollectionDictionary

/**
  * Created by jeorch on 18-3-22.
  */
object alCommonTest extends App {

    val company2 = "8ee0ca24796f9b7f284d931650edbd4b"
    val temp1 = "06a65fc5-7e50-45cf-97e1-59beef62e8fe"
    val temp2 = "a2c5e077-4231-4e74-9c11-8f17391c0ab0"
    val temp3 = "bf4575e1-e6da-4d23-b6b1-b3d3e5b66a2d"

//    alCollectionDictionary(s"${company2}_dictionary","201712","INF", s"$company2$temp1").putNewItem
    alCollectionDictionary(s"${company2}_dictionary","201702","INF", s"$company2$temp1").putNewItem
    alCollectionDictionary(s"${company2}_dictionary","201710","INF", s"$company2$temp2").putNewItem
    alCollectionDictionary(s"${company2}_dictionary","201702","INF", s"$company2$temp3").putNewItem

}
