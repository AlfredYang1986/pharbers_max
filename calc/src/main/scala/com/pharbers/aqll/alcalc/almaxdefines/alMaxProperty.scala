package com.pharbers.aqll.alcalc.almaxdefines

/**
  * Created by Alfred on 11/03/2017.
  */
case class alMaxProperty(val parent : String,
                         val uuid : String,
                         val subs : List[alMaxProperty],
                         val sum1 : Double,
                         val sum2 : Double,
                         val sum3 : Double)
