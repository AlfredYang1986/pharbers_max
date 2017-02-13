package com.pharbers.aqll.calc.stub

object Test extends App{
    
//    def mydef(fn: Int => Int): Int = {
//        fn(10)
//    }
//    
//    println(mydef { x => x + 2 })
//    
//    val myfun = (x: Int) => x * 2
//    
//    val number = List(1, 2, 3, 4, 5)
//    
//    def oMap(list: List[Int], fun: Int => Int): List[Int] = {
//        list.map ( x => x * 2 + fun(10))
//    }
//    
//    println(oMap(number, myfun))
//    
//    
//    def myfunlist(list: List[String], fun: String => String): List[String] = {
//        list.distinct.map { x => fun("Hello , ") + x }
//    }
//    
//    println(myfunlist(List("sss","sss","aa"), (x: String) => x))
    
//    val funsum = (x: List[Double]) => x.sum
//    
//    def sum(data: List[(String,Double)], fun: List[Double] => Double): List[(String,Double)] = {
//        data map { x => 
//            (x._1,fun(data map(_._2)))
//        }
//    }
    
//    val list = List(("aa",12.5),("bb",10.5),("cc",15.5))
//    
//    println(sum(list,funsum))
    
    val aa = ((12,"北京协和医院","三级甲等") :: (13,"上海华山医院","三级甲等") :: Nil)
    
    println(aa)
    
}
