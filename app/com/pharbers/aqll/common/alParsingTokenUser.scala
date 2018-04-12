package com.pharbers.aqll.common

import java.util.Date

import com.mongodb.casbah.Imports._
import com.pharbers.cliTraits.DBTrait
import com.pharbers.driver.redis.phRedisDriver
import com.pharbers.token.AuthTokenTrait
import module.auth.AuthModule.r2m
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

trait ParsingTokenUserTrait {
	implicit val d2m: DBObject => Map[String, JsValue] = { obj =>
		val reVal = obj.getAs[MongoDBObject]("profile").getOrElse(obj.as[MongoDBObject]("reg_content"))
		Map("email" -> toJson(reVal.getAs[String]("email").map(x => x).getOrElse("")),
			"name" -> toJson(reVal.getAs[String]("name").map(x => x).getOrElse(reVal.as[String]("linkman"))),
			"phone" -> toJson(reVal.getAs[String]("phone").map(x => x).getOrElse("")),
			"scope" -> toJson(reVal.getAs[List[String]]("scope").map(x => x).getOrElse(Nil)))
	}
}

case class User (name: String,
                 email: String,
                 phone: String,
                 scope: List[String]) extends TokenAction(3, "userdetails")

case class alParsingTokenUser(token: String)(implicit att: AuthTokenTrait, db: DBTrait) extends ParsingTokenUserTrait {
	def parse: TokenAction = {
		val reVal = att.decrypt2JsValue(token)
		val expire_in = (reVal \ "expire_in").asOpt[Long].map (x => x).getOrElse(throw new Exception("token parse error"))
		if(new Date().getTime > expire_in) TokenFail()
		else {

			val reMap = db.queryObject(DBObject("user_id" -> (reVal \ "user_id").as[String]), "users") match {
				case None => db.queryObject(DBObject("reg_id" -> (reVal \ "user_id").as[String]), "reg_apply")
				case one => one
			}
			User(reMap.get.get("name").map(x => x.as[String]).getOrElse(""),
				 reMap.get.get("email").map(x => x.as[String]).getOrElse(""),
				 reMap.get.get("phone").map(x => x.as[String]).getOrElse(""),
				 reMap.get.get("scope").map(x => x.as[List[String]]).getOrElse(Nil))
		}
	}
}

case class alParsingTokenUser2(token: String)(implicit att: AuthTokenTrait, db: DBTrait) extends ParsingTokenUserTrait {

	val redisDriver = phRedisDriver().commonDriver

	def parse: TokenAction = {
		val auth = redisDriver.hgetall1(token).getOrElse(Map())
		if (auth.isEmpty) TokenFail()
		else {
			val reVal = toJson(r2m(auth))
			User((reVal \ "name").asOpt[String].getOrElse(""),
				(reVal \ "email").asOpt[String].getOrElse(""),
				(reVal \ "phone").asOpt[String].getOrElse(""),
				(reVal \ "scope").asOpt[List[String]].getOrElse(Nil))
		}
	}
}