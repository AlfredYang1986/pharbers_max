package com.pharbers.aqll.alCalcOther.alMessgae

import com.pharbers.aqll.common.alErrorCode.alErrorCode
import com.pharbers.aqll.common.alFileHandler.emChatConfig._
import io.swagger.client.api.{AuthenticationApi, MessagesApi}
import io.swagger.client.model.{Msg, Token}
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.stm.{Ref, atomic}

/**
  * Created by qianpeng on 2017/5/24.
  */

sealed trait alEmChat{
	val alOrgName = orgName
	
	val alAppName = appName
	
	val grant_type = grantType
	
	val client_id = clientId
	
	val client_secret = clientSecret
	
	val access = Ref(Map[String, Any]())
	
	val api = new MessagesApi
	
	def initTokenByProp() = {
		val map = Json.parse(new AuthenticationApi().
			orgNameAppNameTokenPost(alOrgName,
				alAppName,
				new Token().clientId(client_id).
					grantType(grant_type).
					clientSecret(client_secret))).as[Map[String, JsValue]]
		atomic {implicit tnx =>
			access() = access() ++ Map("access_token" -> (" Bearer " + map.get("access_token").get.asOpt[String].getOrElse(alErrorCode.getErrorMessageByCode(-406))),
				"expirdat" -> (System.currentTimeMillis() + map.get("expires_in").get.asOpt[Double].getOrElse(-1D)))
		}
	}
	
	protected def invokeEasemobAPI(payload: Msg): String = {
		api.orgNameAppNameMessagesPost(alOrgName, alAppName, getAccessToken, payload)
	}
	
	def getAccessToken(): String = {
		if(access.single.get.isEmpty ||
			System.currentTimeMillis() > access.single.get.get("expirdat").get.asInstanceOf[Double])
			initTokenByProp()
		access.single.get.get("access_token").get.toString
	}
}

trait alMessage extends alEmChat{
	
	def sendMsg(toUser: String, content: String, ext: Map[String, String] = Map.empty): Boolean
	
	def addUser(): Boolean = ???
	
	def updataUser(): Boolean = ???
	
	def delUser(): Boolean = ???
	
}
