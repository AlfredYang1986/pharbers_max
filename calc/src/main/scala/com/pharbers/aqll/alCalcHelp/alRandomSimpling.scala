package com.pharbers.aqll.alCalcHelp

import scala.util.Random

/**
  * Created by jeorch on 17-8-30.
  * 在start到end的范围内，抽取simpleCount个随机样例
  * ///Random test use
  */
trait alRandomSimpling {
    def getRandomSimplingList(start : Int, end : Int, simpleCount : Int) : List[Int] = {
        var list : List[Int] = Nil
        val status :Array[Int] = new Array[Int](end+1)
        val random = new Random()
        var temp : Int = 0
        for (i <- 1 to simpleCount){
            val r : Int = random.nextInt(end) + 1
            if (status(r)==0){
                list = r :: list
                r match {
                    case x if x == end => status(r) = start
                    case _ => status(r) = r + 1
                }
            } else {
                temp = r
                do {
                    temp = status(temp)
                }while (status(temp) != 0)
                list = temp :: list
                temp match {
                    case x if x == end => status(temp) = start
                    case _ => status(temp) = temp + 1
                }
            }

        }
        list
    }
}
