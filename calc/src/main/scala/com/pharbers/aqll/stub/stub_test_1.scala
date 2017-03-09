package com.pharbers.aqll.stub

import com.pharbers.aqll.alcalc.aldata.alStorage
import com.pharbers.aqll.alcalc.alFileHandler.alexcel.alIntegrateddataparser

/**
  * Created by Alfred on 09/03/2017.
  */
object stub_test_1 extends App {
    val s = alStorage("""config/new_test/2016-01.xlsx""", new alIntegrateddataparser)

    println(s.isCalc)
    s.doCalc
    println(s.isCalc)
    println(s.data.length)
}
