package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import akka.actor.{Actor, ActorLogging, ActorRef, FSM, Props}
import akka.remote.routing.RemoteRouterConfig
import akka.routing.{BroadcastGroup, BroadcastPool}
import com.pharbers.aqll.alCalaHelp.alMaxDefines.{alCalcParmary, alMaxProperty}
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger.{filter_excel_job_2, push_calc_job_2, push_group_job, push_split_excel_job}
import com.pharbers.aqll.alCalcOther.alLog.alLoggerMsgTrait
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.queryIdleNodeInstanceInSystemWithRole
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcData.{calc_data_end, calc_slave_status}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoFilterExcel.filter_excel_end
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoGroupData.{group_data_end, group_data_error}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoMaxDriver.{max_calc_done, push_filter_job, push_split_job}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoSplitExcel.split_excel_end
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxMaster
import com.pharbers.aqll.alMSA.alMaxCmdMessage._
import com.pharbers.aqll.common.alFileHandler.fileConfig.{fileTarGz, memorySplitFile, scpPath, sync}
import com.pharbers.aqll.common.alFileHandler.serverConfig.{serverHost106, serverHost50, serverUser}
import com.pharbers.aqll.alCalaHelp.dbcores._
import com.pharbers.aqll.alCalcOther.alMessgae.alMessageProxy
import com.pharbers.aqll.alCalcOther.alfinaldataprocess.{alRestoreColl, alWeightSum}


trait alMaxDriverTrait { this : Actor =>

	def push_filter_job_impl(file: String, cp: alCalcParmary) = {
		val act = context.actorOf(alCameoMaxDriver.props)
		act ! push_filter_job(file, cp)
	}
	
	def max_calc_done_impl(mp: String Map String) = {
		val act = context.actorOf(alCameoMaxDriver.props)
		act ! max_calc_done(mp)
	}
}

trait alPointState
case object alDriverJobIdle extends alPointState
case object split_excel extends alPointState
case object group_file extends alPointState
case object calc_maxing extends alPointState
case object calc_done extends alPointState

trait alCameoMaxDriverTrait2 extends ActorLogging with FSM[alPointState, alCalcParmary]
	                                              with alLoggerMsgTrait { this: Actor =>
	val acts = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/driver-actor")
	var path = ""
	var almp: alMaxProperty = alMaxProperty("", "", Nil)
	
	def cmdActor: ActorRef = context.actorOf(alCmdActor.props())

	startWith(alDriverJobIdle, alCalcParmary("", ""))
	when(alDriverJobIdle) {
		case Event(push_filter_job(file, cp), pr) => {
			pr.company = cp.company
			pr.uname = cp.uname
			path = file
			acts ! filter_excel_job_2(file, cp)
			stay()
		}
		
		case Event(filter_excel_end(r, cp), pr) => {
			pr.market = cp.market
			pr.year = cp.year
			self ! push_split_job(path)
			alMessageProxy().sendMsg("15", pr.uname, Map("uuid" -> "", "company" -> pr.company, "type" -> "progress"))
			goto(split_excel) using pr
		}
		
		case Event(max_calc_done(mp), pr) =>
			self ! max_calc_done(mp)
			goto(calc_done) using pr
	}
	
	when(split_excel) {
		case Event(push_split_job(file), pr) => {
			acts ! push_split_excel_job(file, pr)
			stay()
		}
		case Event(split_excel_end(r, u, subs, cp), pr) => {
			// TODO: 先发送压缩命令
			pr.uuid = u
			val sub = subs map (x => alMaxProperty(u, x, Nil))
			val mp = alMaxProperty(null, u, sub)
//			almp = mp
//			cmdActor ! pkgmsg(s"${memorySplitFile}${sync}$u" :: Nil, s"${memorySplitFile}${fileTarGz}$u")
//			stay()
			alMessageProxy().sendMsg("15", pr.uname, Map("uuid" -> u, "company" -> pr.company, "type" -> "progress"))
			self ! push_group_job(mp)
			goto(group_file) using pr
		}
		
		case Event(pkgend(s), pr) => {
			// TODO: 压缩命令结束后，Stop压缩Actor
			// TODO: 发送SCP命令
			context stop s
			//			(s"${memorySplitFile}${fileTarGz}${pr.uuid}.tar.gz", s"${scpPath}", serverHost106, serverUser) ::
			(s"${memorySplitFile}${fileTarGz}${pr.uuid}.tar.gz", s"${scpPath}", serverHost106, serverUser) :: Nil foreach ( x => cmdActor ! scpmsg(x._1, x._2, x._3, x._4))
			stay()
		}
		case Event(scpend(s), pr) => {
			// TODO: SCP命令结束后，Stop ScpActor
			context stop s
			self ! push_group_job(almp)
			goto(group_file) using pr
		}
	}
	
	when(group_file) {
		case Event(push_group_job(mp), cp) => {
			acts ! push_group_job(mp)
			stay()
		}
		case Event(group_data_end(r, mp), pr) => {
			alMessageProxy().sendMsg("15", pr.uname, Map("uuid" -> mp.uuid, "company" -> pr.company, "type" -> "progress"))
			self ! push_calc_job_2(mp, pr)
			goto(calc_maxing) using pr
		}
		case Event(group_data_error(reason), pr) => {
			new alMessageProxy().sendMsg("100", pr.uname, Map("error" -> s"error with actor=${self}, reason=${reason}"))
			shutCameo()
			goto(alDriverJobIdle) using new alCalcParmary("", "")
		}
	}
	
	when(calc_maxing) {
		case Event(push_calc_job_2(mp, cp), pr) => {
			acts ! push_calc_job_2(mp, cp)
			stay()
		}
		case Event(calc_data_end(r, mp), pr) => {
			println(mp.finalValue)
            println(mp.finalUnit)
			finalSuccessWithWork(pr, mp)
			acts ! calc_slave_status()
			alMessageProxy().sendMsg("100", pr.uname, Map("uuid" -> mp.uuid, "company" -> pr.company, "type" -> "progress_calc"))
			shutCameo()
			goto(alDriverJobIdle) using new alCalcParmary("", "")
		}
	}
	when(calc_done) {
		case Event(max_calc_done(mp), _) =>
			val company = mp.get("company").getOrElse("")
			val uuid = mp.get("uuid").getOrElse("")
			val uname = mp.get("uname").getOrElse("")
			alWeightSum().apply(company, s"$company$uuid")
			alMessageProxy().sendMsg("100", uname, Map("uuid" -> uuid, "company" -> company, "type" -> "progress_calc_result"))
			dbc.getCollection(company + s"$company$uuid").drop()
			shutCameo()
			goto(alDriverJobIdle) using new alCalcParmary("", "")
	}
	
	whenUnhandled {
		case Event(_, _) => {
			println("unknown")
			shutCameo()
			stay()
		}
	}

	def finalSuccessWithWork(cp : alCalcParmary, property : alMaxProperty) = {
        property.subs.map{ p =>
            p.isCalc = true
            alRestoreColl().apply(s"${cp.company}${property.uuid}", p.uuid :: Nil)
        }
        property.isCalc = true
    }
	
	def shutCameo() = {
		log.info("stopping temp cameo END")
		context.stop(self)
	}
}

object alCameoMaxDriver {
	case class push_filter_job(file: String, cp: alCalcParmary)
	case class push_split_job(path : String)
	case class max_calc_done(mp: String Map String)
	def props = Props[alCameoMaxDriver]
}

class alCameoMaxDriver extends Actor with ActorLogging
									 with alCameoMaxDriverTrait2{
	import alCameoMaxDriver._
}