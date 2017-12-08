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

case class alMaxRunning(var uid: String,
                        var tid: String,
                        var parent: String,
                        var subs: List[alMaxRunning] = Nil,
                        var isSumed : Boolean = false,
                        var sum : List[(String, (Double, Double, Double))] = Nil,
                        var finalValue : Double = 0.0,
                        var finalUnit : Double = 0.0,
                        var result: Boolean = true) extends java.io.Serializable