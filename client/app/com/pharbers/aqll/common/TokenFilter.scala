package com.pharbers.aqll.common

import java.util.Date
import javax.inject.Inject
import com.mongodb.casbah.Imports._
import akka.stream.Materializer
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.driver.redis.phRedisDriver
import com.pharbers.token.AuthTokenTrait
import module.auth.AuthModule.r2m
import play.api.http.HttpFilters
import play.api.libs.json.Json.toJson
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}

class Filters @Inject()(token: TokenFilter) extends HttpFilters {
    override def filters: Seq[EssentialFilter] = {
        Seq(token)
    }
}

trait FilterTrait extends Filter {
    def getCookies(requestHeader: RequestHeader): Map[String, String] = {
        requestHeader.headers.get("Cookie") match {
            case Some(s) => s.split(";").map { cookie =>
                val temp = cookie.split("=")
                if (temp.length == 2) {
                    (temp(0), temp(1))
                } else {
                    throw new Exception("用户禁用Cookie")
                }
            }.toMap[String, String]
            case None => Map()
        }
    }
}

class TokenFilter @Inject()(implicit val mat: Materializer, ec: ExecutionContext,
                            att: AuthTokenTrait, dbt: dbInstanceManager) extends FilterTrait with ParsingTokenUserTrait{
    implicit val db = dbt.queryDBInstance("cli").get

    //恳请杨总不杀
    val noLogingUrlMapping1 = "/register" :: "/auth/" :: "/phonecode/" :: "/validation/token/" :: "/email/invocation/" :: "/password/" ::  Nil
    val noLogingUrlMapping2 = "/" :: "/login" :: "/test" :: "/akka/callback" :: "/order/success" :: "/token/fail" ::
                                "/login/confirm" :: "/user/changepwd" :: "/user/forgetWithPassword" :: Nil
//                                "/calc/querySelectBox" :: "/calc/querySalesVsShare" :: "/calc/queryCurVsPreWithCity"
    val bdUrlMapping = "/login/db" :: "/bd/bdUser" :: "/register/querybd" :: "/register/update" :: "/bd/addMember" ::
                        "/user/querypage" :: "/user/push" :: "/user/delete" :: Nil

    def apply(nextFilter: RequestHeader => Future[Result])
             (requestHeader: RequestHeader): Future[Result] = {

        val startTime = System.currentTimeMillis
        def requestSuccess(result: Result) = result.withHeaders("Request-Time" -> (System.currentTimeMillis - startTime).toString)
        nextFilter(requestHeader).map { result =>
               val targetURL = requestHeader.uri
               if(targetURL.startsWith("/assets/"))
                   requestSuccess(result)
               else if(noLogingUrlMapping1.exists(x => targetURL.startsWith(x)))
                   requestSuccess(result)
               else if(noLogingUrlMapping2.exists(x => targetURL.equals(x)))
                   requestSuccess(result)
               else{
                   val accessToken = getCookies(requestHeader).get("user_token").map(x => x).getOrElse("")
                   if (accessToken.isEmpty)
                       Results.Redirect(Call("GET", "/"))
                   else {
                       validationToken2(accessToken) match {
                           case TokenFail() => Results.Redirect(Call("GET", "/"))
                           case User(_, _, _, scope) =>
                               if(scope.contains("NC") && bdUrlMapping.contains(targetURL)) Results.NotFound
                               else requestSuccess(result)
                           case _ => requestSuccess(result)
                       }
                   }
               }
        }
    }

    def validationToken(token: String): TokenAction = {
        val reVal = att.decrypt2JsValue(token)
        val expire_in = (reVal \ "expire_in").asOpt[Long].map(x => x).getOrElse(throw new Exception("token parse error"))
        if (new Date().getTime > expire_in) TokenFail()
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

    def validationToken2(accessToken: String): TokenAction = {
        val redisDriver = phRedisDriver().commonDriver
        val token = redisDriver.hgetall1(accessToken).getOrElse(Map())
        if (token.isEmpty) TokenFail()
        else {
            val reVal = toJson(r2m(token))
            User((reVal \ "name").asOpt[String].getOrElse(""),
                (reVal \ "email").asOpt[String].getOrElse(""),
                (reVal \ "phone").asOpt[String].getOrElse(""),
                (reVal \ "scope").asOpt[List[String]].getOrElse(Nil))
        }
    }
}