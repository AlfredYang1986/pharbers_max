package com.pharbers.aqll.alCalcOther.alEmchat

import io.swagger.client.ApiException
import io.swagger.client.api.AuthenticationApi
import io.swagger.client.model.Token
import com.pharbers.aqll.alCalaHelp.emChatConfig._
import com.pharbers.aqll.common.alErrorCode.alErrorCode
import play.api.libs.json.{JsValue, Json}

/**
  * Created by qianpeng on 2017/4/13.
  */

trait EasemobAPI {
	def invokeEasemobAPI(): String
}

object alOrgInfo {

	val alOrgName = orgName

	val alAppName = appName
}

object ResponseHandler {
	def handle(easemobAPI: EasemobAPI): String = {
		try {
			easemobAPI.invokeEasemobAPI()
		} catch {
			case e: ApiException => {
				e.getCode match {
					case 400 => "400"
					case 401 =>
						TokenUtil.initTokenByProp
						easemobAPI.invokeEasemobAPI
					case 429 => "429"
					case 500 =>
						val r = retry(easemobAPI)
						if(retry(easemobAPI) != null) r else "500"
					case x => x.toString
				}
			}
		}
	}

	def retry(easemobAPI: EasemobAPI): String = {
		var time = 5
		var r = ""
		for(i <- 1 to 2) {
			try {
				println(s"Reconnection is in progress... $i")
				val r = easemobAPI.invokeEasemobAPI
				if(r != null) r
			} catch {
				case e: ApiException => time *= 3
				case e2: InterruptedException => println(e2.getMessage)
			}
		}
		r
	}
}

object TokenUtil {
	val grant_type = grantType
	val client_id = clientId
	val client_secret = clientSecret
	val body = new Token().clientId(client_id).grantType(grant_type).clientSecret(client_secret)
	val api = new AuthenticationApi
	var access_token = ""
	var expirdat = -1D

	def initTokenByProp() = {
		val resp = api.orgNameAppNameTokenPost(alOrgInfo.alOrgName, alOrgInfo.alAppName, body)
		val map = Json.parse(resp).as[Map[String, JsValue]]
		access_token = " Bearer " + map.get("access_token").get.asOpt[String].getOrElse(alErrorCode.getErrorMessageByCode(-406))
		expirdat = (System.currentTimeMillis() + map.get("expires_in").get.asOpt[Double].getOrElse(0.0))
	}

	def getAccessToken(): String = {
		if(access_token == "" || isExpired) {
			initTokenByProp
		}
		access_token
	}

	def isExpired(): Boolean = {
		System.currentTimeMillis() > expirdat
	}
}
