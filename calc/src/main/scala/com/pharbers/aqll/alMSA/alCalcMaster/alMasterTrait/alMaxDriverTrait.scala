package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import java.util.Date

import play.api.libs.json.{JsValue, Json}

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

case class EmChatMessage() {
	def creatreEmRooms(company: String, uid: String): String = {
		val roomName = s"${company}_${uid}"
		val roomId = (Json.parse(EmChatMsg().getAllRooms) \ "data").as[List[String Map JsValue]].find(x => x("name").as[String] == roomName) match {
			case None => {
				val reVal = (Json.parse(EmChatMsg()
				  .setRoomName(roomName)
				  .setRoomDescription(roomName)
				  .setRoomOnwer("project")
				  .setRoomMaxUsers(200)
				  .createChatRoom) \ "data").as[String Map JsValue]
				reVal("id").as[String]
			}
			case Some(x) => x("id").as[String]
		}

		(Json.parse(EmChatMsg().getUsersBatch()) \ "entities").as[List[String Map JsValue]].filter(x =>
			x("username").as[String].indexOf(s"${company}_") != -1 && x("username").as[String].indexOf(s"_${uid}") != -1
		).map(x => x("username").as[String]) match {
			case Nil => ""
			case lst => EmChatMsg().setRoomMembers(roomId, lst)
		}
	}
	
	def sendEMMessage(company: String, uid: String, uuid: String, fileName: String, mestype: String, step: String, msg: String): String = {
		val reVal = (Json.parse(EmChatMsg().getAllRooms) \ "data").as[List[String Map JsValue]]
			.filterNot(x => x("name").as[String] != company + "_" + uid)
			.map(x => x("id").as[String])
		
		EmChatMsg().sendFromUser("project")
			.sendTargetUser(reVal)
			.sendTargetType("chatrooms")
			.sendMsgContentType()
			.sendMsgExt(Map("file" -> fileName, "uuid" -> uuid, "table" -> s"${company + uuid}", "type" -> mestype, "step" -> step))
			.sendMsg(msg)
	}
}

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

			acts ! filter_excel_job_2(file, cp)

			EmChatMessage().sendEMMessage(pr.company, pr.uid, pr.uuid, pr.fileName, "progress_calc", "过滤文件中", "1")
			stay()
		}
		
		case Event(filter_excel_end(r, cp), pr) => {
			pr.market = cp.market
			pr.year = cp.year
			self ! push_split_job(path)
			EmChatMessage().sendEMMessage(pr.company, pr.uid, pr.uuid, pr.fileName, "progress_calc", "过滤文件结束", "10")
			goto(split_excel) using pr
		}
		
		case Event(max_calc_done(mp), pr) =>
			self ! max_calc_done(mp)
			goto(calc_done) using pr
	}
	
	when(split_excel) {
		case Event(push_split_job(file), pr) => {
			acts ! push_split_excel_job(file, pr)
			EmChatMessage().sendEMMessage(pr.company, pr.uid, pr.uuid, pr.fileName, "progress_calc", "分拆文件中", "15")
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

			EmChatMessage().sendEMMessage(pr.company, pr.uid, pr.uuid, pr.fileName, "progress_calc", "分拆文件结束", "18")

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
		case Event(push_group_job(mp), pr) => {
			acts ! push_group_job(mp)
			EmChatMessage().sendEMMessage(pr.company, pr.uid, pr.uuid, pr.fileName, "progress_calc", "文件分组中", "20")

			stay()
		}
		
		case Event(group_data_end(r, mp), pr) => {
			pr.uuid = mp.uuid
			EmChatMessage().sendEMMessage(pr.company, pr.uid, pr.uuid, pr.fileName, "progress_calc", "等待计算", "25")

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
//			new alMessageProxy().sendMsg("100", pr.imuname, Map("error" -> s"error with actor=${self}, reason=${reason}"))
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
			EmChatMessage().sendEMMessage(pr.company, pr.uid, pr.uuid, pr.fileName, "progress_calc", "准备还原数据库", "90")
			mp.isCalc = true
			goto(restore_maxing) using pr
		}
	}
	
	when(restore_maxing) {
		case Event(restore_bson_end(result, sub_uuid), pr) => {
			EmChatMessage().sendEMMessage(pr.company, pr.uid, pr.uuid, pr.fileName, "progress_calc", "还原数据库结束", "100")
			acts ! calc_slave_status()
			test_num = test_num + 1
//			alMessageProxy().sendMsg("100", pr.imuname, Map("file" -> pr.fileName, "uuid" -> pr.uuid, "table" -> s"${pr.company + pr.uuid}", "type" -> "progress_calc", "step" -> "计算结束"))
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
			val uid = mp.get("uid").getOrElse("")
			
			EmChatMessage().sendEMMessage(company, uid, uuid, "", "progress_calc_result", "正在转储为永久数据中", "20")
			
//			alMessageProxy().sendMsg("20", imuname, Map("uuid" -> uuid, "company" -> company, "type" -> "progress_calc_result", "step" -> "正在转储为永久数据中"))
			alWeightSum().apply(company, s"$company$uuid")
			
			EmChatMessage().sendEMMessage(company, uid, uuid, "", "progress_calc_result", "成功", "100")
			println("成功")
//			alMessageProxy().sendMsg("100", imuname, Map("uuid" -> uuid, "company" -> company, "type" -> "progress_calc_result", "step" -> "正在转储为永久数据中"))
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