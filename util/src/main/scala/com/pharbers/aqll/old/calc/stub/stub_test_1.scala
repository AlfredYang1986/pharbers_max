package com.pharbers.aqll.old.calc.stub

import java.util.{Calendar, Date}

import akka.actor.ActorRef
import com.google.gson.Gson
import com.pharbers.aqll.old.calc.alcalc.alFileHandler.altext.FileOpt
import com.pharbers.aqll.old.calc.alcalc.alSchedulerJobs.alScheduleRemoveFiles
import com.pharbers.aqll.old.calc.alcalc.alemchat.{alIMUser, sendMessage}
import com.pharbers.aqll.old.calc.alcalc.allog.alLoggerMsgTrait

/**
  * Created by Alfred on 09/03/2017.
  */
object stub_test_1 extends App{

	val a: ActorRef = null
//	def func(more: Int) = (x: Int) => x + more
//	val inc = func(10)
//	println(inc(10))

//	def func2(args: Map[String, String]*) = {
//		args.map (x => x(x.keys.head))
//	}
//	println(func2(Map("key1" -> "a"), Map("key2" -> "b"), Map("key2" -> "c")))

	//	logger.info("大的撒大")

	//	val path = "/Users/qianpeng/Desktop/Test\\"
	//	FileOpt(path).rmaAllFiles

//	alScheduleRemoveFiles.rmLst foreach (println _)

//	val d = new Date
//	printf("Hours: %s, Seconds: %s", d.getHours, d.getSeconds)

//	val time = Calendar.getInstance
//
//	printf("Hours: %s, Seconds: %s", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.SECOND))

//	def test(str: => String) = str
//
//	println(test(""))

//	val uuid = "1234567890987654321"
//	val company = "BMS"
//	val r = sendMessage.sendMsg("10", "test2", Map("type" -> "txt"))
//	println(r)

//	val progress = 2
//	val c = s"""{"uuid": "$uuid", "company": "$company", "progress": $progress}"""
//	val result = sendMessage.send(uuid, company, progress , "test2")
//	println(result)

//	val name = "BMS"
//	val pwd = "123"
//	alIMUser.createUser(name, pwd)
//	alIMUser.changePwd(name, "sadada")

	//{
	//// test case 1 : reading excel file and storage and portion
	//val s = alStorage(config/new_test/2016-01.xlsx""", new alIntegrateddataparser)
	//val ps = s.portion { lst =>
	//lst.grouped(10).map(iter => alPortion(iter)).toList
	//}
	//println(ps.isCalc)
	//println(ps.portions.head)
	//}

	//{
	//// test case 2 : reading memory and map func
	//val s = alStorage(List(1, 2, 3, 4, 5, 6, 7, 8, 9))
	//val s1 = s.map { x =>
	//x.asInstanceOf[Int] + 1
	//}
	//
	//val ps = s1.portion { lst =>
	//lst.grouped(2).map(iter => alPortion(iter)).toList
	//}
	//println(ps.portions.head)
	//}

	//    {
	//        // test case 3 : persistence test
	//        val s = alStorage("""config/new_test/2016-01.xlsx""", new alIntegrateddataparser)
	//        val ps = s.portion { lst =>
	//            lst.grouped(10).map(iter => alPortion(iter)).toList
	//        }
	//        alTextSync("""config/sync""", s)
	//        alTextSync("""config/sync/po""", ps)
	//        println(ps.portions.head)
	//    }

	//{
	//// test case 4 : restore data form text
	//val s = alStorage("""config/sync/po/0ae964e41b9bda2ff21045533bbcdda9""", new alTextParser)
	//println(s.isCalc)
	//s.doCalc
	//println(s.data)
	//}

	//{
	//// test case 5 : compress data ** gzip
	//val p = new pkgCmd("""config/group/ad0046ee-4669-4ed3-a781-cea9995c97f4""" :: Nil, """config/compress/test2/aa""")
	//p.excute
	//}
	//
	//{
	// test case 6 : restore from compress file ** gzip
	//val lst = FileOpt("/Users/qianpeng/Desktop/Test").lstFiles
	//lst foreach { x =>
	//new unPkgCmd(s"${x.substring(0, x.lastIndexOf("tar")-1)}", "/Users/qianpeng/Desktop/Test2").excute
	//}
	//val path = "/Users/qianpeng/Desktop/Test2"
	//if(!FileOpt(path).isDir) FileOpt(path).createDir

	//val p = new unPkgCmd("""config/compress/test""", """config/compress""")
	//p.excute
	//}

	//    {
	//        // test case 7 : scp ** gzip // 需要提前部署scp rsa秘钥
	//        val p = new scpCmd("""config/compress/test.tar.gz""", """""", "59.110.31.215", "root")
	//        p.excute
	//    }

	//{
	//// test case 8 : cp ** gzip
	//val p = new cpCmd("""config/compress/test.tar.gz""", """~/Desktop/test""")
	//p.excute
	//}

	//{
	//
	//cur = Some(new pkgCmd("""config/group/ad0046ee-4669-4ed3-a781-cea9995c97f4""" :: Nil, """config/compress/test2/aa""") :: Nil)
	//process = do_pkg() :: Nil
	//super.excute()
	////a.cc("你好")
	////a.cc("你好2")
	//}

	{
//			FileOpt("""/Users/qianpeng/Desktop/Test/""").rmAllFiles
//		val a = FileOpt("""/Users/qianpeng/Desktop/scp2""").rmAllFiles
//		println(a)

		//val a1 = "aaa你好"
		//val a2 = "aaaaaa你好"
		//val b1 = "bbb你好啊"
		//println(a1.hashCode)
		//println(a2.hashCode)
		//println(b1.hashCode)


		//val local_price=new BigDecimal(0.015)
		//val exchange_rate = new BigDecimal(2)
		////0.02999999999999999888977697537484345957636833190917968750
		//println(local_price.multiply(exchange_rate))
		//
		////0.030
		//import scala.math.BigDecimal
		////0.0319998
		//val a = BigDecimal(0.0159999092321049857939849) /  BigDecimal(2.900998764537)
		//println(a.getClass)
		//println(a)
		//println(a.toDouble)
		//println(0.0159999092321049857939849 / 2.900998764537)
		//
		//println(BigDecimal("0.0") *  BigDecimal("0.751879699"))

		//t(new alCalcParmary("BMS"))
		//println(ListBuffer((2015, "BMS"), (2015, "BMS"), (2016, "BMS")) distinct)

//		println(MD5.md5("Pain"))

//		_data_connection_thread.getCollection("test").drop()
	}

	//def t(c: alCalcParmary): Unit = {
	//println(c.year)
	//c.year = 2017
	//println(c.year)
	//println(c.market)
	//println(c.company)
	//println(c.uuid)
	//}

//	FileOpt("""/Users/qianpeng/Desktop/scp""").rmAllFiles

	implicit def Int2Number(t: java.lang.Integer): Int = t.intValue
}

//object a {
//def cc(s:String) = {
//new bb(s).t()
//}
//}
//
//class bb(s:String) {
//def t() ={
//println(s)
//}
//}