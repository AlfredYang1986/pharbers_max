package com.pharbers.aqll.alCalcEnergy

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.util.Timeout
import com.pharbers.aqll.alCalc.almain.alShareData
import com.pharbers.aqll.common.alCmd.pkgcmd.{pkgCmd, unPkgCmd}
import com.pharbers.aqll.common.alCmd.scpcmd.scpCmd

import scala.concurrent.Await
import scala.concurrent.stm.{Ref, atomic}
import scala.concurrent.duration._
import akka.pattern.ask
import com.pharbers.aqll.alCalaHelp.alMaxDefines.{alCalcParmary, alMaxProperty}
import com.pharbers.aqll.alCalcMemory.aljobs.alJob.{common_jobs, split_group_jobs}
import com.pharbers.aqll.alCalcMemory.aljobs.alPkgJob
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger._
import com.pharbers.aqll.alCalcMemory.alprecess.alprecessdefines.alPrecessDefines._
import com.pharbers.aqll.alCalcMemory.alstages.alStage
import com.pharbers.aqll.common.alFileHandler.fileConfig._
import com.pharbers.aqll.common.alFileHandler.serverConfig._
import com.pharbers.aqll.alCalc.almodel.java.IntegratedData
import com.pharbers.aqll.alCalcMemory.aldata.alStorage

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by qianpeng on 2017/5/17.
  */
trait alGroupJobsSchedule { this: Actor =>
	val waiting_grouping = Ref(List[alMaxProperty]())     // only for waiting jobs
	val grouping_jobs = Ref(List[alMaxProperty]())     // only for calcing jobs
	val group_timer = context.system.scheduler.schedule(0 seconds, 1 seconds, self, new schedule_group)
}

trait alGroupJobsManager extends alPkgJob { this: Actor with alGroupJobsSchedule with ActorLogging=>
	val group_router = Ref(List[ActorRef]())
	var group_nodenumber = -1

	def registerGroupRouter(a : ActorRef) = atomic { implicit txn =>
		group_router() = group_router() :+ a
	}

	def pushGroupJobs(cur : alMaxProperty) = atomic { implicit txn =>
		waiting_grouping() = waiting_grouping() :+ cur
	}

	def scheduleOneGroupJob = atomic { implicit txn =>
		waiting_grouping() match {
			case head :: lst => {
				if (canSignGroupJob(head))
					signGroupJob(head)
			}
			case Nil => Unit
		}
	}

	def successWithGroup(uuid : String, sub_uuid : String) = {
		grouping_jobs.single.get.find(x => x.uuid == uuid).map (x => Some(x)).getOrElse(None) match {
			case None => Unit
			case Some(r) => {
				r.subs.find (x => x.uuid == sub_uuid).map (x => x.grouped = true).getOrElse(Unit)

				cur = Some(new unPkgCmd(s"${root + scpPath + sub_uuid}", s"${root + program}") :: Nil)
				process = do_pkg() :: Nil
				super.excute()

				if (r.subs.filterNot (x => x.grouped).isEmpty) {
					val common = common_jobs()
					//                    common.cur = Some(alStage(r.subs map (x => s"config/group/${x.uuid}")))
					//                    val a = r.subs map(_.uuid)



					common.cur = Some(alStage(r.subs map (x => s"${memorySplitFile}${group}${x.uuid}")))
					common.process = restore_grouped_data() ::
						do_calc() :: do_union() :: do_calc() ::
						do_map (alShareData.txt2IntegratedData(_)) :: do_calc() :: Nil

					common.result

					val concert = common.cur.get.storages.head.asInstanceOf[alStorage]
					val m = alStorage.groupBy (x =>
						(x.asInstanceOf[IntegratedData].getYearAndmonth, x.asInstanceOf[IntegratedData].getMinimumUnitCh)
					)(concert)

					val g = alStorage(m.values.map (x => x.asInstanceOf[alStorage].data.head.toString).toList)
					g.doCalc
					val sg = alStage(g :: Nil)
					val pp = presist_data(Some(r.uuid), Some("group"))
					pp.precess(sg)
					
					log.info("done for grouping")

					groupJobSuccess(uuid)
				}
			}
		}
	}

	def groupJobSuccess(uuid : String) = {
		grouping_jobs.single.get.find(x => x.uuid == uuid).map (x => Some(x)).getOrElse(None) match {
			case None => Unit
			case Some(r) => {
				atomic { implicit tnx =>
					grouping_jobs() = grouping_jobs().tail
				}
				// 分拆计算文件
				val spj = split_group_jobs(Map(split_group_jobs.max_uuid -> r.uuid))
				val (p, sb) = spj.result.map (x => x.asInstanceOf[(String, List[String])]).getOrElse(throw new Exception("split grouped error"))
				val subs = sb map (x => alMaxProperty(p, x, Nil))
				
				log.info(s"calc is uuid = $uuid")
				cur = Some(pkgCmd(s"${memorySplitFile}${calc}$uuid" :: Nil, s"${memorySplitFile}${fileTarGz}$uuid")
					:: scpCmd(s"${memorySplitFile}${fileTarGz}$uuid.tar.gz", s"${scpPath}", serverHost106, serverUser)
					:: scpCmd(s"${memorySplitFile}${fileTarGz}$uuid.tar.gz", s"${scpPath}", serverHost50, serverUser)
					:: Nil)
				process = do_pkg() :: Nil
				super.excute()
				self ! finish_max_group_job(uuid)
				self ! push_calc_job(alMaxProperty(null, p, subs))
			}
		}
	}

	def groupJobFailed(uuid : String) = {

	}

	def canSignGroupJob(p : alMaxProperty): Boolean = {
		implicit val t = Timeout(0.5 second)
		val f = group_router.single.get map (x => x ? can_sign_job())
		p.subs.length <= (f.map (x => Await.result(x, 0.5 seconds)).count(x => x.isInstanceOf[sign_job_can_accept]))
	}

	def signGroupJob(p : alMaxProperty) = {
		siginEach(group_router.single.get)
		atomic { implicit tnx =>
			waiting_grouping() = waiting_grouping().tail
			grouping_jobs() = grouping_jobs() :+ p
		}

		def siginEach(lst: List[ActorRef]): Unit = {
			lst match {
				case Nil => log.info("not enough group to do the jobs")
				case node => {
					group_nodenumber = group_nodenumber + 1
					lst.head ! concert_groupjust_result(group_nodenumber)
					alCalcParmary.alParmary.single.get.find(_.uuid == p.uuid) match {
						case None => log.info("not GroupParamry file")
						case Some(x) =>
							lst.head ! group_job(p, x)
							siginEach(lst.tail)
					}
				}
				case _ => ???
			}
		}
	}
}
