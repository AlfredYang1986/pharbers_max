package com.pharbers.aqll.common

import java.util.Date

import com.pharbers.driver.redis.phRedisDriver
import com.pharbers.token.AuthTokenTrait
import module.auth.AuthModule.r2m
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

class TokenAction(val num: Int, val str: String)
case class TokenError() extends TokenAction(-1, "error")
case class TokenFail() extends TokenAction(0, "tokefail")
case class TokenForgetPassword() extends TokenAction(1, "forgetpassword")
case class TokenFirstLogin() extends TokenAction(2, "firstlogin")

case class alValidationToken(token: String)(implicit att : AuthTokenTrait) {
	def validation: TokenAction = {
		val reVal = att.decrypt2JsValue(token)
		val expire_in = (reVal \ "expire_in").asOpt[Long].map (x => x).getOrElse(throw new Exception("token parse error"))
		expire_in match {
			case n if new Date().getTime > n => TokenFail()
			case _ =>
				(reVal \ "action").asOpt[String].getOrElse(None) match {
					case None => TokenError()
					case "forget_password" => TokenForgetPassword()
					case "first_login" => TokenFirstLogin()
					case _ => throw new Exception("token content action error")
				}
		}
	}
}
case class alValidationToken2(accessToken: String) {
	def validation: TokenAction = {
		val redisDriver = phRedisDriver().commonDriver
		val token = redisDriver.hgetall1(accessToken).getOrElse(Map())
		if (token.isEmpty) TokenFail()
		else {
			val reVal = toJson(r2m(token))
			(reVal \ "action").asOpt[String].getOrElse(None) match {
				case None => TokenError()
				case "forget_password" => TokenForgetPassword()
				case "first_login" => TokenFirstLogin()
				case _ => throw new Exception("token content action error")
			}
		}
	}
}