//package com.pharbers.aqll.alMSA.alCalcMaster
//
//import akka.actor.{Actor, ActorLogging, Props}
//
//
//class alMaxDriver extends Actor with ActorLogging
//								with alMaxDriverTrait{
//
//	import alMaxDriver._
//
//	override def receive: Receive = {
//		case push_filter_job(file, cp) => println("Start Filter panel文件位置 = " + file); push_filter_job_impl(file, cp)
//		case max_calc_done(mp) => max_calc_done_impl(mp)
//
//		case _ => ???
//	}
//}
