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

class alSendMessage extends alSendMessageTrait {
	val responseHandler = ResponseHandler
	val api = new MessagesApi

	override def sendMsg(payload: Msg): String = {
		ResponseHandler.handle(new EasemobAPI {
			override def invokeEasemobAPI(): String = api.orgNameAppNameMessagesPost(alOrgInfo.alOrgName,alOrgInfo.alAppName,TokenUtil.getAccessToken, payload)
		})
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
		resultIMException(result)
	}
}
