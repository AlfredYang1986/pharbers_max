package com.pharbers.aqll.alStart.alEntry

import java.io.ByteArrayOutputStream
import java.util.Base64
import java.util.zip.{Deflater, GZIPOutputStream}

import akka.actor.ActorSystem
import akka.cluster.Cluster
import com.pharbers.aqll.alCalaHelp.alMaxDefines.{alCalcParmary, alMaxProperty, endDate, startDate}
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger.{push_calc_job_2, push_group_job, push_split_excel_job}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoGroupData.group_data_end
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoMaxDriver.push_filter_job
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoSplitExcel.split_excel_end
import com.pharbers.aqll.common.alFileHandler.fileConfig._
import com.typesafe.config.ConfigFactory
import akka.pattern.ask
import akka.util.Timeout
import com.pharbers.aqll.alCalc.almain.alShareData
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcData.calc_data_end
import com.pharbers.aqll.common.alCmd.pycmd.pyCmd
import com.pharbers.aqll.common.alEncryption.alEncryptionOpt
import com.pharbers.aqll.common.alFileHandler.alFilesOpt.alFileOpt

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.Await

object alActorTest extends App {
	val config = ConfigFactory.load("split-test")
	val system : ActorSystem = ActorSystem("calc", config)
	val cp = new alCalcParmary("fea9f203d4f593a96f0d6faa91ba24ba", "jeorch")
//	implicit val timeout = Timeout(30 minute)

	if(system.settings.config.getStringList("akka.cluster.roles").contains("splittest")) {
		Cluster(system).registerOnMemberUp {
			val a = system.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/portion-actor")
			val path = fileBase + "2017-05.xlsx"

			1 to 20 foreach(x => a ! push_filter_job(path, cp))
//			a ! push_filter_job(path, cp)


//			val f = a ? push_split_excel_job(path, cp)
//			val r = Await.result(f, 2 minute).asInstanceOf[split_excel_end]
//
//			val p = r.uuid
//			val subs = r.subs map (x => alMaxProperty(p, x, Nil))
//			val mp = alMaxProperty(null, p, subs)
//
//			cp.uuid = r.uuid
//
//			val fg = a ? push_group_job(mp)
//			val rg = Await.result(fg, 2 minute).asInstanceOf[group_data_end]
//
//			val fff = a ? push_calc_job_2(mp, cp)
//			val rrr = Await.result(fff, 40 minute).asInstanceOf[calc_data_end]
//
//	        println(rrr.property.finalValue)
//	        println(rrr.property.finalUnit)
		}
	}
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
	
//	val path = s"/Users/qianpeng/Desktop"
//
//	val source = alFileOpt(path + "/" + "data")
//	source.enumDataWithFunc { line =>
//		val mrd = alShareData.txt2WestMedicineIncome2(line)
//
//		if (mrd.ifPanelAll == "1") {
//			if(mrd.phaid == "PHA0021108") {
//				if(mrd.minimumUnitCh == "恩利粉针剂25MG1辉瑞制药有限公司") {
//					println(s"mrd back => phaid = ${mrd.phaid}  ， sumValue = ${mrd.sumValue}  ， sumUnits = ${mrd.volumeUnit}  ,  product = ${mrd.minimumUnitCh}")
//				}
//			}
//		}
//	}

//	val a = List(1,2,3)
//	val b = List("a","b","c")
//	val c = a union b
//	val d = c.sliding(3,1).toList.flatten
//	println(d)

//	pyCmd(s"~/FileBase/fea9f203d4f593a96f0d6faa91ba24ba",Upload_Secondstep_Filename, "201611#").excute
}
