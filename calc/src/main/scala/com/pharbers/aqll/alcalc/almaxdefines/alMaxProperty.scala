package com.pharbers.aqll.alcalc.almaxdefines

/**
  * Created by Alfred on 11/03/2017.
  */
case class alMaxProperty(val parent : String,
                         val uuid : String,
                         val subs : List[alMaxProperty],
                         var signed : Boolean = false,
                         var grouped : Boolean = false,
                         var sum1 : Double = 0.0,
                         var sum2 : Double = 0.0,
                         var sum3 : Double = 0.0)
