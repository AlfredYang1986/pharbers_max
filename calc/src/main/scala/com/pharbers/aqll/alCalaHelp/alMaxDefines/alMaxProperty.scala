package com.pharbers.aqll.alCalaHelp.alMaxDefines

/**
  * Created by Alfred on 11/03/2017.
  */
case class alMaxProperty(parent : String,
                         uuid : String,
                         var subs : List[alMaxProperty],
                         var signed : Boolean = false,
                         var grouped : Boolean = false,
                         var isSumed : Boolean = false,
                         var sum : List[(String, (Double, Double, Double))] = Nil,
                         var isCalc : Boolean = false,
                         var finalValue : Double = 0.0,
                         var finalUnit : Double = 0.0
                        ) extends java.io.Serializable


case class alCalcStep() extends Enumeration with java.io.Serializable{
    val ONLINE = Value(0)
    val YM = Value(1)
    val PANEL = Value(2)
    val FILTER = Value(3)
    val SPLIT = Value(4)
    val GROUP = Value(5)
    val CALC = Value(6)
    val RESTORE = Value(7)
    val STOP = Value(-1)
}

case class alMaxRunning(panel: String = "", cid: String, parent: String, uid: String) extends java.io.Serializable