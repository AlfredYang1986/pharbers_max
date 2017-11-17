package com.pharbers.aqll.alCalcEnergy

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.util.Timeout
import com.pharbers.aqll.common.alDao.dataFactory._

import scala.concurrent.Await
import scala.concurrent.stm.{Ref, atomic}
import scala.concurrent.duration._
import akka.pattern.ask
import com.pharbers.aqll.alCalaHelp.alMaxDefines.{alCalcParmary, alMaxProperty, endDate}
import com.pharbers.aqll.alCalcOther.alMail.{EmailForCompany, Mail, StmConf}
import com.pharbers.aqll.common.alFileHandler.mailConfig._
import com.pharbers.aqll.alCalcMemory.aljobs.alPkgJob
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger._
import com.pharbers.alCalcMemory.alprecess.alsplitstrategy.server_info
import com.pharbers.aqll.alCalcOther.alMessgae.{alWebSocket}
import com.pharbers.aqll.alCalcOther.alfinaldataprocess.{alRestoreColl, alWeightSum}
import com.pharbers.aqll.alCalcOther.alfinaldataprocess.alWeightSum

import scala.concurrent.ExecutionContext.Implicits.global
import com.pharbers.aqll.alCalaHelp.dbcores._
import com.pharbers.aqll.alCalcEnergy.alAkkaMonitoring.alAkkaMonitor._

import scala.collection.immutable.Map

/**
  * Created by qianpeng on 2017/5/17.
  */
trait alCalcJobsSchedule { this: Actor =>
	val waiting_calc = Ref(List[alMaxProperty]())     // only for waiting jobs
	val calcing_jobs = Ref(List[alMaxProperty]())     // only for calcing jobs
	val calc_timer = context.system.scheduler.schedule(0 seconds, 1 seconds, self, new schedule_calc)
}

trait alCalcJobsManager extends alPkgJob { this: Actor with alCalcJobsSchedule with ActorLogging =>
//	val calc_router = Ref(List[ActorRef]())
	var calc_nodenumber = -1
	var section_number = -1

//	def registerCalcRouter(a : ActorRef) = atomic { implicit txn =>
//		calc_router() = calc_router() :+ a
//	}

	def pushCalcJobs(cur : alMaxProperty) = atomic { implicit txn =>
		waiting_calc() = waiting_calc() :+ cur
	}

	def scheduleOneCalcJob = atomic { implicit txn =>
		waiting_calc() match {
			case head :: lst => {
				if (canSignCalcJob(head))
					signCalcJob(head)
			}
			case Nil => Unit
		}
	}

	def canSignCalcJob(p : alMaxProperty): Boolean = {
		implicit val t = Timeout(0.5 second)
//		val f = calc_router.single.get map (x => x ? can_sign_job())
		val f = calcRouter map (x => x ? can_sign_job())
		p.subs.length / server_info.cpu <= (f.map (x => Await.result(x, 0.5 seconds)).count(x => x.isInstanceOf[sign_job_can_accept]))
	}

	def signCalcJob(p : alMaxProperty) = {
		atomic { implicit tnx =>
//			siginEach(calc_router.single.get)
			siginEach(calcRouter.toList)
			waiting_calc() = waiting_calc().tail
			calcing_jobs() = calcing_jobs() :+ p
		}

		def siginEach(lst: List[ActorRef]): Unit = {
			lst match {
				case Nil => log.info("not enough calc to do the jobs")
				case node => {
					calc_nodenumber = calc_nodenumber + 1
					lst.head ! concert_calcjust_result(calc_nodenumber)
					alCalcParmary.alParmary.single.get.find(_.uuid == p.uuid) match {
						case None => log.info("not CalcParamry file")
						case Some(x) =>
							lst.head ! calc_job(p, x)
							siginEach(lst.tail)
					}
				}
				case _ => ???
			}
		}
	}

	def sumSuccessWithWork(uuid : String, sub_uuid : String, sum : List[(String, (Double, Double, Double))]) = {
		import scala.math.BigDecimal
		calcing_jobs.single.get.find(x => x.uuid == uuid).map (x => Some(x)).getOrElse(None) match {
			case None => Unit
			case Some(r) => {
				
				log.info(s"sum in singleton $sum with $sub_uuid")

				r.subs.find (x => x.uuid == sub_uuid).map { x =>
					x.isSumed = true
					x.sum = sum
				}.getOrElse(Unit)

				if (r.subs.filterNot (x => x.isSumed).isEmpty) {
					val tmp = r.subs.map (x => x.sum).flatten
					log.info(s"done for suming ${tmp.filter(_._1 == "98")}")
					r.sum = (tmp.groupBy(_._1) map { x =>
						(x._1, (x._2.map(z => z._2._1).sum, x._2.map(z => z._2._2).sum, x._2.map(z => z._2._3).sum))
					}).toList
					r.isSumed = true
					log.info(s"done for suming ${r.sum}")

					val mapAvg = r.sum.map { x =>
						(x._1, (BigDecimal((x._2._1 / x._2._3).toString).toDouble),(BigDecimal((x._2._2 / x._2._3).toString).toDouble))
					}

//					calc_router.single.get foreach ( x => x ! calc_avg_job(r.uuid, mapAvg))
					calcRouter foreach ( x => x ! calc_avg_job(r.uuid, mapAvg))
				}
			}
		}
	}

	def finalSuccessWithWork(uuid : String, sub_uuid : String, v : Double, u : Double , start: Long) = {
		calcing_jobs.single.get.find(x => x.uuid == uuid).map (x => Some(x)).getOrElse(None) match {
			case None => Unit
			case Some(r) => {
				r.subs.find (x => x.uuid == sub_uuid).map { x =>
					x.isCalc = true
					x.finalValue = v
					x.finalUnit = u
				}.getOrElse(Unit)

				val company = alCalcParmary.alParmary.single.get.find(_.uuid == uuid) match {
					case None =>
						log.info(s"not company")
//						alRestoreColl("", sub_uuid :: Nil)
						("", "", "")
					case Some(x) =>
						val u = x.company+uuid
						alRestoreColl().apply(u, sub_uuid :: Nil)
						(x.company, u, x.imuname)
					case _ => ???
				}

				val msg = Map(
					"type" -> "progress",
					"progress" -> "2"
				)
				alWebSocket(uuid).post(msg)
				
//				new alMessageProxy().sendMsg("2", company._3, Map("uuid" -> uuid, "company" -> company._1, "type" -> "progress"))
				
				if (r.subs.filterNot (x => x.isCalc).isEmpty) {
					val msg2 = Map(
						"type" -> "progress",
						"progress" -> "10"
					)
					alWebSocket(uuid).post(msg2)

//					new alMessageProxy().sendMsg("10", company._3, Map("uuid" -> uuid, "company" -> company._1, "type" -> "progress"))
					r.finalValue = r.subs.map(_.finalValue).sum
					r.finalUnit = r.subs.map(_.finalUnit).sum
					r.isCalc = true
					log.info(s"done calc job with uuid ${r.uuid}, final value : ${r.finalValue} and final unit : ${r.finalUnit}")
					implicit val stmc = StmConf()
					new Mail().sendTo(EmailForCompany(company._1).getEmail())
					endDate("计算完成",start)

					val msg3 = Map(
						"type" -> "progress_calc",
						"progress" -> "100"
					)
					alWebSocket(uuid).post(msg3)
//					new alMessageProxy().sendMsg("100", company._3, Map("uuid" -> uuid, "company" -> company._1, "type" -> "progress_calc"))
					self ! finish_max_job(uuid)
					atomic { implicit tnx =>
						calcing_jobs() = calcing_jobs().tail
					}
				}
			}
		}
	}

	def commit_finalresult_jobs_func(company: String, uuid: String) = {
		alCalcParmary.alParmary.single.get.find(_.company.equals(company)) match {
			case None => log.info(s"commit_finalresult_jobs_func not company")
			case Some(x) =>
//				new alMessageProxy().sendMsg("30", x.imuname, Map("uuid" -> x.uuid, "company" -> company, "type" -> "progress"))
				log.info(s"x.uuid = ${x.uuid}")
				alWeightSum().apply(company, company + x.uuid)
//				new alMessageProxy().sendMsg("20", x.imuname, Map("uuid" -> x.uuid, "company" -> company, "type" -> "progress"))
				log.info(s"开始删除临时表")
				dbc.getCollection(company + x.uuid).drop()
				log.info(s"结束删除临时表")
				atomic { implicit txn =>
					alCalcParmary.alParmary() = alCalcParmary.alParmary.single.get.filterNot(_.company.equals(x.company))
				}

				val msg = Map(
					"type" -> "progress_calc_result",
					"progress" -> "100"
				)
				alWebSocket(uuid).post(msg)
//				new alMessageProxy().sendMsg("100", x.imuname, Map("uuid" -> uuid, "company" -> company, "type" -> "progress_calc_result"))
		}
	}

	def check_excel_jobs_func(company: String,filename: String) = {
		alCalcParmary.alParmary.single.get.find(_.company.equals(company)) match {
			case None => log.info(s"commit_finalresult_jobs_func not company")
			case Some(x) => log.info(x.company)
		}
	}

}
