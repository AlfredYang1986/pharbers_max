package com.pharbers.aqll.alStart.alEntry

import akka.actor.ActorSystem
import akka.cluster.Cluster
import com.pharbers.aqll.alCalaHelp.alMaxDefines.{alCalcParmary, endDate, startDate}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoMaxDriver.push_filter_job
import com.pharbers.aqll.common.alFileHandler.fileConfig._
import com.typesafe.config.ConfigFactory
import com.pharbers.aqll.alCalc.almain.alShareData

object alActorTest extends App {
//	val config = ConfigFactory.load("split-test")
//	val system : ActorSystem = ActorSystem("calc", config)
//	val cp = new alCalcParmary("fea9f203d4f593a96f0d6faa91ba24ba", "jeorch")
//
//	if(system.settings.config.getStringList("akka.cluster.roles").contains("splittest")) {
//		Cluster(system).registerOnMemberUp {
//			val a = system.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/portion-actor")
//			val path = fileBase + "2017-05.xlsx"
//
//			1 to 20 foreach(x => a ! push_filter_job(path, cp))
////			a ! push_filter_job(path, cp)
//		}
//	}
// 	val s1 = startDate()
// 	var t = ""
// 	val a = "201705" + "纷乐片剂100MG14上海医药集团股份有限公司" + "北京" + "北京市" + "PHA0000001" + "INF"
// 	val c = a :: Nil
// 	//new String(Base64.getEncoder.encode(a))
////	val temp = a.getBytes("UTF-8")
////	val aa = new String(Base64.getEncoder.encode(temp))
////	println(aa.substring(0, aa.length - 1))
//	1 to 1000 foreach { x =>
//		println(a.hashCode.toString)
//	}
////	println(a.hashCode)

// 		c foreach { x =>
// //			val out = new  ByteArrayOutputStream
// //			val b = new GZIPOutputStream(out)
// //			b.write(a.getBytes())
// //			b.close()
// //			println(out.toString("UTF-8"))
// //			println(out.toString("UTF-8").length)
// 	//		val a = alEncryptionOpt.md5("201705" + "纷乐片剂100MG14上海医药集团股份有限公司")
// 	//		t = a
// 	//		if(t == a ) {Unit}
// //			alEncryptionOpt.md5(a)
// 			val temp = Base64.getEncoder.encode(x.getBytes())
// 			val str = new String(temp)
// 			println(str)
// 		}
// 	endDate("e1", s1)
	val str = "Users/qianpeng/FileBase/fea9f203d4f593a96f0d6faa91ba24ba/Output/8ca42876-d01c-4943-9634-1fda4cbfcac2"
	println(str.substring(str.lastIndexOf('/') + 1))
	
}
