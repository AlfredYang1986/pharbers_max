package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import akka.actor.{Actor, ActorLogging, FSM, Props}
import com.pharbers.aqll.alCalaHelp.alMaxDefines.{alCalcParmary, alMaxProperty}
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger.{filter_excel_job_2, push_calc_job_2, push_group_job, push_split_excel_job}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcData.calc_data_end
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoFilterExcel.filter_excel_end
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoGroupData.group_data_end
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoMaxDriver.{push_filter_job, push_job, push_split_job}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoSplitExcel.split_excel_end
import com.pharbers.aqll.alMSA.alClusterLister.alMaxAgentEnergy._


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

trait alCameoMaxDriverTrait2 extends ActorLogging with FSM[alPointState, alCalcParmary]{this: Actor =>
	val acts = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/driver-actor")
	startWith(alDriverJobIdle, new alCalcParmary("", ""))
	when(alDriverJobIdle) {
		case Event(push_filter_job(file, cp), pr) => {
			acts ! filter_excel_job_2(file, cp)
			stay()
		}
		case Event(filter_excel_end(r, file, cp), pr) => {
			pr.uuid = cp.uuid
			pr.company = cp.company
			pr.market = cp.market
			pr.uname = cp.uname
			pr.year = cp.year
			self ! push_split_job(file, cp)
			goto(split_excel) using pr
		}
	}
	
	when(split_excel) {
		case Event(push_split_job(file, cp), pr) => {
			acts ! push_split_excel_job(file, cp)
			stay()
		}
		case Event(split_excel_end(r, u, subs, cp), pr) => {
			// TODO: 向各个机器上发送文件，执行计算文件 sync文件夹下的
			pr.uuid = u
			val sub = subs map (x => alMaxProperty(u, x, Nil))
			val mp = alMaxProperty(null, u, sub)
			self ! push_group_job(mp)
			goto(group_file) using pr
		}
	}
	
	when(group_file) {
		case Event(push_group_job(mp), cp) => {
			acts ! push_group_job(mp)
//			agentEnergy() foreach ( x =>  context.actorSelection(x + "/user/driver-actor") ! push_group_job(mp))
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
//			agentEnergy() foreach ( x =>  context.actorSelection(x + "/user/driver-actor") ! push_calc_job_2(mp, cp))
			stay()
		}
		case Event(calc_data_end(r, mp), pr) => {
			shutCameo()
			goto(alDriverJobIdle) using new alCalcParmary("", "")
		}
	}
	
	whenUnhandled {
		case Event(_, _) => {
			println("fuck")
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
	case class push_split_job(path : String, p: alCalcParmary)
	
	def props = Props[alCameoMaxDriver]
}

class alCameoMaxDriver extends Actor with ActorLogging
									 with alCameoMaxDriverTrait2{
	import alCameoMaxDriver._
}