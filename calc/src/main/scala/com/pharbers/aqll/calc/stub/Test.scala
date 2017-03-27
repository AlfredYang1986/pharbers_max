package com.pharbers.aqll.calc.stub

object Test extends App {

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

	//    val aa = ((12,"北京协和医院","三级甲等") :: (13,"上海华山医院","三级甲等") :: Nil)
	//
	//    println(aa)

	//    println(MD5.md5("INF Market"))

//	def t: String = {
//		try {
//			var ip = ""
//			val netInterfaces = NetworkInterface.getNetworkInterfaces()
//			while (netInterfaces.hasMoreElements()) {
//				val ni: NetworkInterface = netInterfaces.nextElement()
//				if(ni.getDisplayName().equals("en0")){
//					val ips = ni.getInetAddresses()
//					while (ips.hasMoreElements()) {
//						if(ips.nextElement().getHostAddress().indexOf(":") > 0) {
//							ip = ips.nextElement().getHostAddress()
//						}
//					}
//				}
//			}
//			ip
//		} catch {
//			case ex: Exception => ???
//		}
//	}
//	println(t)
//	override val args: Array[String] = Array.empty
//	println(args)
//	println(Const.DB)
//	val property = System.getProperty("dbname")
//	Const.DB = Some(property).getOrElse("Max_Cores")
//	println(Const.DB)

//	println(MD5.md5("AI_R"))

//	val a = "bb" -> "cc"
//	println(a)

//	def t(str: String) = {
//		println("fuck in t")
//		str
//	}
//	def tt(str: => String) = {
//		println("fuck in tt")
//		println(s"asdsadasd $str")
//	}
//	tt(t("aaa"))

//	case class myclass(map: Map[String, String]) {
//		println("啊哈")
//	}
//    type T = myclass
//	val name= new T(Map("name" -> "cQian"))
//	println(name.map.get("name").getOrElse("no"))

//	def listFirst[T](x: List[T]) = {
//		x(0)
//	}
//	println(listFirst(List(1,2,3,4,5)))

//	val f = (name: String) => {
//		println(s"my name is $name")
//		name
//	}
//	f("钱鹏")
//
//	def getName(func: String => String, name: String) = {
//		println(s"is name $name")
//		func(name)
//	}
//
//	getName(f, "啊哈")


//	val lst =  List(1,2,3,4,5,6,7,8,9,10)
//	val grouplst = lst.grouped(2)
//	grouplst foreach{ x =>
//		x foreach (println _)
//	}
//	trait da{
//		val a: String => String
//	}
//
//	def aa(b: String => String, str: String) = {
//		println(b(""))
//		println(str)
//	}
//
//	object cc extends da {
//		override val a: String => String = { str =>
//			str + "Hello"
//		}
//	}
//
//	aa(cc.a, "你好")

	val t = new test
	t.aha("你好")

}

class test {
	val ps = "aa"
	println(ps)

	def aha(str: => String) = {
		println(str)
	}
}