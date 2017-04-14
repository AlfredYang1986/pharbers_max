package com.pharbers.aqll.alcalc.alemchat


import com.google.gson.internal.LinkedTreeMap
import com.pharbers.aqll.alcalc.alCommon._
import io.swagger.client.api.MessagesApi
import io.swagger.client.model.{Msg, MsgContent, UserName}

/**
  * Created by qianpeng on 2017/4/13.
  */
trait alSendMessageTrait {
	def sendMsg(payload: Msg): String
}

class alSendMessage extends alSendMessageTrait with EasemobAPI {
	val responseHandler = ResponseHandler
	val api = new MessagesApi
	var p: Msg = null
	override def sendMsg(payload: Msg): String = {
		p = payload
		ResponseHandler.handle(this)
	}

	override def invokeEasemobAPI() = {
		api.orgNameAppNameMessagesPost(alOrgInfo.alOrgName,alOrgInfo.alAppName,TokenUtil.getAccessToken, p)
	}
}

object sendMessage {
	def send(uuid: String, company: String, progress: Int, uname: String): String = {
		val c = s"""{"uuid": "$uuid", "company": "$company", "progress": $progress}"""
		val msg = new Msg
		val msgContent = new MsgContent
		msgContent.`type`(MsgContent.TypeEnum.TXT).msg(c)
		val userName = new UserName
		userName.add(uname)
		msg.from("project").target(userName).targetType("users").msg(msgContent)
		val result = new alSendMessage().sendMsg(msg)
		result match {
			case "400" => println("400"); "message is error"
			case "401" => println("401"); "unkown error"
			case "429" => println("429"); "unkown error"
			case "500" => println("500"); "server us error"
			case x => {
				alFromJson.formJson(x).get("data").asInstanceOf[LinkedTreeMap[String, String]].get(uname)
			}
		}
	}
}
