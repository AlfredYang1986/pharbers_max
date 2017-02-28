package com.pharbers.aqll.calc.split

import akka.actor.ActorRef
import com.pharbers.aqll.calc.maxmessages.freeMaster
import com.pharbers.aqll.calc.maxresult.Insert

import scala.concurrent.stm.Ref
import scala.concurrent.stm.atomic

case class job_para(val filename: String,
                    val sub_name: String,
                    var bSigned: Boolean,
                    var faultTimes: Int = 0,
                    var sum: List[(String, (Double, Double, Double))] = Nil,
                    var isProsscedData: Boolean = false,
                    val maxTryTimes: Int = 3)

object SplitJobsContainer {
	val jobs = Ref(List[job_para]()) // filename, sub-filenames, fault times

	def isJobCalcing(f: String): Boolean = jobs.single.get.find(x => x.filename == f).map(_ => true).getOrElse(false)

	def isJobSigned(f: String): Boolean = jobs.single.get.find(x => x.filename == f).map(_.bSigned).getOrElse(false)

	def isProsscedData(f: String, s: String): Boolean = jobs.single.get.find(x => x.filename == f && x.sub_name == s).map(_.isProsscedData).getOrElse(false)

	def pushJobs(f: String, s: List[String]) = atomic { implicit thx =>
		val sub = s.map(x => job_para(f, x, false))
		jobs() = jobs() ++: sub
	}

	def queryJobSubNamesWithName(f: String): List[String] =
		jobs.single.get.filter(x => x.filename == f).map(x => x.sub_name)

	def jobFailed(f: String, s: String) = {
		val v = jobs.single.get
		v.find(x => x.filename == f && x.sub_name == s).map { x =>
			val n = job_para(f, s, x.bSigned, x.faultTimes + 1, x.sum, false, x.maxTryTimes)

			if (n.faultTimes > 3) {
				// TODO： 失败三次算失败，不再计算，给出提示
				atomic { implicit thx =>
					jobs() = jobs().filterNot(y => y.filename == f && y.sub_name == s) :+ n
				}
			} else {
				// TODO: 一个小程序失败所有的节点全部算失败
				popJob(f, false)
			}
		}.getOrElse(Unit)
	}

	def popJob(f: String, success: Boolean, message: String = "") = {

		atomic { implicit thx =>
			jobs() = jobs().filterNot(y => y.filename == f)
		}

		if (success) {
			// TODO：计算成功，正常流程删除

		} else {
			// TODO: 计算失败，非正常流程删除

		}
	}

	def handleProcesedDataMessage(f: String, s: String, id: String, company: String): Boolean = {
		jobs.single.get.find(x => x.filename == f && x.sub_name == s) match {
			case None => false // error
			case Some(x) => {
				if (!x.isProsscedData) {
					val n = job_para(f, s, x.bSigned, x.faultTimes, x.sum, true, x.maxTryTimes)
					atomic { implicit thx =>
						jobs() = jobs().filterNot(y => y.filename == f && y.sub_name == s) :+ n
					}


					if (jobs.single.get.filter(x => x.filename == f && x.isProsscedData == false) == Nil) {
						new Insert().groupByResutInsert(id, company)
					}
					true

				} else {
					// TODO: error
					println("Error")
					false
				}
			}
		}
	}


	def pushRequestAverage(f: String, s: String, sum: List[(String, (Double, Double, Double))]): (Boolean, List[(String, Double, Double)]) = {
		jobs.single.get.find(x => x.filename == f && x.sub_name == s) match {
			case None => (false, Nil)
			case Some(x) => {
				val n = job_para(f, s, x.bSigned, x.faultTimes, sum, false, x.maxTryTimes)
				atomic { implicit thx =>
					jobs() = jobs().filterNot(y => y.filename == f && y.sub_name == s) :+ n
				}

				if (jobs.single.get.filter(x => x.filename == f && x.sum == Nil) == Nil) {
					val unionSum = jobs.single.get.filter(x => x.filename == f).map(_.sum).flatten

					val sumAll = unionSum.groupBy(_._1) map { x =>
						(x._1, (x._2.map(z => z._2._1).sum, x._2.map(z => z._2._2).sum, x._2.map(z => z._2._3).sum))
					}

					val mapAvg = sumAll map { x =>
						(x._1, (x._2._1 / x._2._3), (x._2._2 / x._2._3))
					}
					println(s"mapAvg = ${mapAvg}")
					(true, mapAvg.toList)
				} else (false, Nil)
			}
		}
	}
}
