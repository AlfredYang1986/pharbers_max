package com.pharbers.aqll.alCalcHelp.alMaxDefines

/**
  * Created by Alfred on 11/03/2017.
  */
//TODO zhujian shanchu
case class alMaxRunning(var uid: String,
                        var tid: String,
                        var parent: String,
                        var subs: List[alMaxRunning] = Nil,
                        var isSumed : Boolean = false,
                        var sum : List[(String, (Double, Double, Double))] = Nil,
                        var finalValue : Double = 0.0,
                        var finalUnit : Double = 0.0,
                        var result: Boolean = true) extends java.io.Serializable