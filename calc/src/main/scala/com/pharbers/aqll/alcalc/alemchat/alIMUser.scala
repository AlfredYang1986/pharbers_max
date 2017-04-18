package com.pharbers.aqll.alcalc.alemchat

import com.pharbers.aqll.alcalc.alCommon._
import io.swagger.client.api.UsersApi
import io.swagger.client.model.{NewPassword, RegisterUsers, User}

/**
  * Created by qianpeng on 2017/4/18.
  */
trait alIMUserTrait {
	def createNewIMUserSingle(payload: RegisterUsers): String

	def updataIMUserSingle(userName: String, payload: NewPassword): String

}

class alIMUser extends alIMUserTrait {
	val responseHandler = ResponseHandler
	val api = new UsersApi

	def createNewIMUserSingle(payload: RegisterUsers): String = {
		ResponseHandler.handle(new EasemobAPI {
			override def invokeEasemobAPI(): String = api.orgNameAppNameUsersPost(alOrgInfo.alOrgName,alOrgInfo.alAppName, payload, TokenUtil.getAccessToken)
		})
	}

	def updataIMUserSingle(userName: String, payload: NewPassword): String = {
		ResponseHandler.handle(new EasemobAPI {
			override def invokeEasemobAPI(): String = api.orgNameAppNameUsersUsernamePasswordPut(alOrgInfo.alOrgName,alOrgInfo.alAppName, userName, payload, TokenUtil.getAccessToken)
		})
	}
}

object alIMUser {
	def createUser(name: String, pwd: String): String = {
		val users = new RegisterUsers
		val user = new User().username(name).password(pwd)
		users.add(user)
		val result = new alIMUser().createNewIMUserSingle(users)
		resultIMException(result)
	}

	def changePwd(name: String, pwd: String): String = {
		val psd = new NewPassword().newpassword(pwd)
		val result = new alIMUser().updataIMUserSingle(name, psd)
		resultIMException(result)
	}
}

object resultIMException {
	def apply(str: String) = exc(str)

	def exc(str: String) = {
		str match {
			case "400" => println("400"); "message is error"
			case "401" => println("401"); "unkown error"
			case "429" => println("429"); "unkown error"
			case "500" => println("500"); "server us error"
			case x if x.length > 5 => println(x); ""
			case _ => ???
		}
	}
}
