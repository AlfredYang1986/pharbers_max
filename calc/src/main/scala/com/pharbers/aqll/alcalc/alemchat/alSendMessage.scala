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
	def send(content: String, uname: String): String = {
		val msg = new Msg
		val msgContent = new MsgContent
		msgContent.`type`(MsgContent.TypeEnum.TXT).msg(content)
		val userName = new UserName
		userName.add(uname)
		msg.from("project").target(userName).targetType("users").msg(msgContent)
		val result = alFromJson.formJson(new alSendMessage().sendMsg(msg))
		result.get("data").asInstanceOf[LinkedTreeMap[String, String]].get(uname)
	}
}
