package com.pharbers.aqll.old.calc.alcalc.alemchat

import com.pharbers.aqll.old.calc.alcalc.alCommon._
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

	def sendMsg(msgStr: String, uname: String, ext: Map[String, String] = Map.empty): String = {
		val userName = new UserName()
		userName.add(uname)
		resultIMException(new alSendMessage().sendMsg(new Msg().from("project").target(userName).targetType("users").msg(new MsgContent().`type`(MsgContent.TypeEnum.TXT).msg(msgStr)).ext(ext)))
	}
}
