package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import akka.actor.{Actor, ActorLogging, ActorRef, FSM, Props}
import akka.remote.routing.RemoteRouterConfig
import akka.routing.{BroadcastGroup, BroadcastPool}
import com.pharbers.aqll.alCalaHelp.alMaxDefines.{alCalcParmary, alMaxProperty}
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger.{filter_excel_job_2, push_calc_job_2, push_group_job, push_split_excel_job}
import com.pharbers.aqll.alCalcOther.alLog.alLoggerMsgTrait
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.queryIdleNodeInstanceInSystemWithRole
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcData.calc_data_end
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoFilterExcel.filter_excel_end
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoGroupData.group_data_end
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoMaxDriver.{push_filter_job, push_job, push_split_job}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoSplitExcel.split_excel_end
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxMaster
import com.pharbers.aqll.alMSA.alMaxCmdMessage._
import com.pharbers.aqll.common.alFileHandler.fileConfig.{fileTarGz, memorySplitFile, scpPath, sync}
import com.pharbers.aqll.common.alFileHandler.serverConfig.{serverHost106, serverHost50, serverUser}


trait alMaxDriverTrait { this : Actor =>

	def push_filter_job_impl(file: String, cp: alCalcParmary) = {
		val act = context.actorOf(alCameoMaxDriver.props)
		act ! push_filter_job(file, cp)
	}

}

trait alPointState
case object alDriverJobIdle extends alPointState
case object split_excel extends alPointState
case object group_file extends alPointState
case object calc_maxing extends alPointState

trait alCameoMaxDriverTrait2 extends ActorLogging with FSM[alPointState, alCalcParmary]
	                                              with alLoggerMsgTrait{ this: Actor =>
	val acts = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/driver-actor")
	var path = ""
	
	def cmdActor: ActorRef = context.actorOf(alCmdActor.props())

	startWith(alDriverJobIdle, new alCalcParmary("", ""))
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
			goto(split_excel) using pr
		}
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
			cmdActor ! pkgmsg(s"${memorySplitFile}${sync}$u" :: Nil, s"${memorySplitFile}${fileTarGz}$u")
			//			self ! push_group_job(mp)
			stay()
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
			self ! push_group_job(null)
			stay()
		}
	}
	
	when(group_file) {
		case Event(push_group_job(mp), cp) => {
//			acts ! push_group_job(mp)
			println(s"fuck ====.>>>> push_group_job")
			stay()
		}
		case Event(group_data_end(r, mp), pr) => {
			self ! push_calc_job_2(mp, pr)
			goto(calc_maxing) using pr
		}
	}
	
	when(calc_maxing) {
		case Event(push_calc_job_2(mp, cp), pr) => {
			acts ! push_calc_job_2(mp, cp)
			stay()
		}
		case Event(calc_data_end(r, mp), pr) => {
			shutCameo()
			println(mp.finalValue)
            println(mp.finalUnit)
			goto(alDriverJobIdle) using new alCalcParmary("", "")
		}
	}
	
	whenUnhandled {
		case Event(push_group_job(mp), pr) => {
			println(s"fuck =====>.... ${mp}")
			self ! push_group_job(mp)
			goto(group_file) using pr
		}
		case Event(_, _) => {
			println("unkonw")
			shutCameo()
			stay()
		}
	}
	
	def shutCameo() = {
		println("stopping temp cameo")
		context.stop(self)
	}
}

object alCameoMaxDriver {
	case class push_job(mp: alMaxProperty, cp: alCalcParmary)
	case class push_filter_job(file: String, cp: alCalcParmary)
	case class push_split_job(path : String)
	
	def props = Props[alCameoMaxDriver]
}

class alCameoMaxDriver extends Actor with ActorLogging
									 with alCameoMaxDriverTrait2{
	import alCameoMaxDriver._
}