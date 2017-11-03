package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import java.util.Date

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSelection, FSM, PoisonPill, Props}
import akka.remote.routing.RemoteRouterConfig
import akka.routing.{BroadcastGroup, BroadcastPool}
import com.pharbers.alCalcMemory.alprecess.alsplitstrategy.server_info
import com.pharbers.aqll.alCalaHelp.alMaxDefines.{alCalcParmary, alMaxProperty, endDate, startDate}
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger._
import com.pharbers.aqll.alCalcOther.alLog.alLoggerMsgTrait
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcData.{calc_data_end, calc_slave_status}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoFilterExcel.filter_excel_end
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoGroupData.{group_data_end, group_data_error}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoMaxDriver.{max_calc_done, push_filter_job, push_split_job}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoSplitExcel.split_excel_end
import com.pharbers.aqll.alMSA.alMaxCmdMessage._
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxDriver._
import com.pharbers.aqll.common.alFileHandler.fileConfig._
import com.pharbers.aqll.common.alFileHandler.serverConfig.{serverHost106, serverHost50, serverUser}
import com.pharbers.aqll.alCalaHelp.dbcores._
import com.pharbers.aqll.alCalcOther.alMessgae.alMessageProxy
import com.pharbers.aqll.alCalcOther.alfinaldataprocess._
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoRestoreBson.restore_bson_end
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alScpQueueActor.PushToScpQueue
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.{queryIdleNodeInstanceInSystemWithRole, refundNodeForRole}
import com.pharbers.message.im.EmChatMsg
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.stm.atomic

trait alMaxDriverTrait {
	this: Actor =>
	def push_filter_job_impl(file: String, cp: alCalcParmary) = {
		val act = context.actorOf(alCameoMaxDriver.props)
		act ! push_filter_job(file, cp)
	}
	
	def max_calc_done_impl(mp: String Map String) = {
		val act = context.actorOf(alCameoMaxDriver.props)
		act ! max_calc_done(mp)
	}
}

sealed trait alPointState

case object alDriverJobIdle extends alPointState

case object split_excel extends alPointState

case object group_file extends alPointState

case object calc_maxing extends alPointState

case object restore_maxing extends alPointState

case object calc_done extends alPointState

trait alCameoMaxDriverTrait2 extends ActorLogging with FSM[alPointState, alCalcParmary]
	with alLoggerMsgTrait {
	this: Actor =>
	var almp: alMaxProperty = alMaxProperty("", "", Nil)
	var path = ""
	val acts = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/driver-actor")
	val queueActor = context.actorOf(alScpQueueActor.props(self))
	var s1 = startDate()
	
	import alCameoMaxDriver._
	
	def cmdActor: ActorRef = context.actorOf(alCmdActor.props())
	
	startWith(alDriverJobIdle, alCalcParmary("", ""))
	when(alDriverJobIdle) {
		case Event(push_filter_job(file, cp), pr) => {
			path = file
			pr.company = cp.company
			pr.imuname = cp.imuname
			pr.uid = cp.uid
			pr.fileName = file.substring(file.lastIndexOf('/') + 1)
			
			// TODO 明天提出去
			val roomName = s"${pr.company}_${pr.uid}"
			val reVal = (Json.parse(EmChatMsg()
					.setRoomName(roomName)
					.setRoomDescription(roomName)
					.setRoomOnwer("project")
					.setRoomMaxUsers(200)
					.createChatRoom) \ "data").as[String Map JsValue]
			val roomId = reVal("id").as[String]
			
			(Json.parse(EmChatMsg().getUsersBatch()) \ "entities").as[List[String Map JsValue]].filter(x =>
				x("username").as[String].indexOf(s"${pr.company}_") != -1 && x("username").as[String].indexOf(s"_${pr.uid}") != -1
			).map(x => x("username").as[String]) match {
				case Nil => Unit
				case lst => EmChatMsg().setRoomMembers(roomId, lst)
			}
			
			
			
			acts ! filter_excel_job_2(file, cp)
			alMessageProxy().sendMsg("1", pr.imuname, Map("file" -> pr.fileName, "company" -> pr.company, "type" -> "progress_calc", "step" -> "过滤文件中"))
			stay()
		}
		
		case Event(filter_excel_end(r, cp), pr) => {
			pr.market = cp.market
			pr.year = cp.year
			self ! push_split_job(path)
			alMessageProxy().sendMsg("10", pr.imuname, Map("file" -> pr.fileName, "company" -> pr.company, "type" -> "progress_calc", "step" -> "过滤文件结束"))
			goto(split_excel) using pr
			//			shutCameo()
			//			stay()
		}
		
		case Event(max_calc_done(mp), pr) =>
			self ! max_calc_done(mp)
			goto(calc_done) using pr
	}
	
	when(split_excel) {
		case Event(push_split_job(file), pr) => {
			acts ! push_split_excel_job(file, pr)
			alMessageProxy().sendMsg("15", pr.imuname, Map("file" -> pr.fileName, "company" -> pr.company, "type" -> "progress_calc", "step" -> "分拆文件中"))
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
			alMessageProxy().sendMsg("18", pr.imuname, Map("file" -> pr.fileName, "company" -> pr.company, "type" -> "progress_calc", "step" -> "分拆文件结束"))
			self ! push_group_job(mp)
			goto(group_file) using pr
		}
		
		case Event(pkgend(s), pr) => {
			// TODO: 压缩命令结束后，Stop压缩Actor
			// TODO: 发送SCP命令
			context stop s
			(s"${memorySplitFile}${fileTarGz}${pr.uuid}.tar.gz", s"${scpPath}", serverHost106, serverUser) :: Nil foreach { x =>
				queueActor ! PushToScpQueue(x._1, x._2, x._3, x._4)
			}
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
			alMessageProxy().sendMsg("20", cp.imuname, Map("file" -> cp.fileName, "company" -> cp.company, "type" -> "progress_calc", "step" -> "文件分组中"))
			stay()
		}
		
		case Event(group_data_end(r, mp), pr) => {
			pr.uuid = mp.uuid
			alMessageProxy().sendMsg("25", pr.imuname, Map("file" -> pr.fileName, "company" -> pr.company, "type" -> "progress_calc", "step" -> "等待计算"))
			self ! push_calc_job_2(mp, pr)
			goto(calc_maxing) using pr
			
			// TODO : 发送pkg的压缩Actor
			//			almp = mp
			//			cmdActor ! pkgmsg(s"${memorySplitFile}${group}${mp.uuid}" :: Nil, s"${memorySplitFile}${fileTarGz}${mp.uuid}")
			//			stay()
		}
		
		case Event(pkgend(s), pr) => {
			// TODO: 压缩命令结束后，Stop压缩Actor
			// TODO: 发送SCP命令
			context stop s
			(s"${memorySplitFile}${fileTarGz}${pr.uuid}.tar.gz", s"${scpPath}", serverHost106, serverUser) :: Nil foreach (x => queueActor ! PushToScpQueue(x._1, x._2, x._3, x._4))
			stay()
		}
		
		case Event(scpend(s), pr) => {
			// TODO: SCP命令结束后，Stop ScpActor
			context stop s
			self ! push_calc_job_2(almp, pr)
			goto(calc_maxing) using pr
		}
		
		case Event(group_data_error(reason), pr) => {
			println(s"Error! group_data_error(${reason}, ${pr})")
			new alMessageProxy().sendMsg("100", pr.imuname, Map("error" -> s"error with actor=${self}, reason=${reason}"))
			shutCameo
			goto(alDriverJobIdle) using new alCalcParmary("", "")
		}
	}
	
	when(calc_maxing) {
		case Event(push_calc_job_2(mp, cp), pr) => {
			acts ! push_calc_job_2(mp, cp)
			stay()
		}
		
		case Event(calc_data_end(r, mp), pr) => {
			pr.uuid = mp.uuid
			println(s"mp.finalValue=${mp.finalValue}")
			println(s"mp.finalUnit=${mp.finalUnit}")
			val sub_uuids = mp.subs.map { p =>
				p.isCalc = true
				p.uuid
			}
			acts ! push_restore_job(s"${pr.company}${mp.uuid}", sub_uuids)
			mp.isCalc = true
			goto(restore_maxing) using pr
		}
	}
	
	when(restore_maxing) {
		case Event(restore_bson_end(result, sub_uuid), pr) => {
			acts ! calc_slave_status()
			test_num = test_num + 1
			
			// TODO: 这里有待斟酌
			val reVal = (Json.parse(EmChatMsg().getAllRooms) \ "data").as[List[String Map JsValue]]
				.filterNot(x => x("name").as[String] != pr.company + "_" + pr.uid)
				.map(x => x("id").as[String])
			
			EmChatMsg().sendFromUser("project")
				.sendTargetUser(reVal)
				.sendTargetType("chatrooms")
				.sendMsgContentType()
				.sendMsgExt(Map("uuid" -> sub_uuid, "company" -> pr.company, "type" -> "progress_calc"))
				.sendMsg("100")
			
			//			alMessageProxy().sendMsg("100", pr.imuname, Map("uuid" -> sub_uuid, "company" -> pr.company, "type" -> "progress_calc"))
			endDate("test" + test_num, s1)
			shutCameo()
			goto(alDriverJobIdle) using new alCalcParmary("", "")
		}
	}
	
	when(calc_done) {
		case Event(max_calc_done(mp), _) =>
			val company = mp.get("company").getOrElse("")
			val uuid = mp.get("uuid").getOrElse("")
			val imuname = mp.get("imuname").getOrElse("")
			alMessageProxy().sendMsg("20", imuname, Map("uuid" -> uuid, "company" -> company, "type" -> "progress_calc_result", "step" -> "正在转储为永久数据中"))
			alWeightSum().apply(company, s"$company$uuid")
			alMessageProxy().sendMsg("100", imuname, Map("uuid" -> uuid, "company" -> company, "type" -> "progress_calc_result", "step" -> "转储完成"))
			dbc.getCollection(s"$company$uuid").drop()
			shutCameo
			goto(alDriverJobIdle) using new alCalcParmary("", "")
	}
	
	whenUnhandled {
		case Event(msg, _) => {
			println(s"unknown msg=${msg}")
			stay()
		}
	}
	
	def shutCameo() = {
		s1 = startDate()
		log.info("stopping alMaxDriverTrait cameo END")
		self ! PoisonPill
	}
}

object alCameoMaxDriver {
	
	case class push_filter_job(file: String, cp: alCalcParmary)
	
	case class push_split_job(path: String)
	
	case class max_calc_done(mp: String Map String)
	
	def props = Props[alCameoMaxDriver]
	
	var test_num: Int = 0
	var finalValue: Double = 0
	var finalUnit: Double = 0
}

class alCameoMaxDriver extends Actor with ActorLogging
	with alCameoMaxDriverTrait2 {
}