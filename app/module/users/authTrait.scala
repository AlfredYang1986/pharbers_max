package module.users

import java.util.Date

import com.mongodb.casbah.Imports._
import com.pharbers.bmmessages.CommonModules
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.driver.PhRedisDriverImpl
import com.pharbers.sercuity.Sercurity
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

import scala.collection.immutable.Map

/**
  * Created by spark on 18-4-25.
  */
trait authTrait {

    val authPwd: JsValue => DBObject = { js =>
        $and(
            DBObject("email" -> (js \ "condition" \ "email").asOpt[String].map(x => x).getOrElse("")),
            DBObject("password" -> (js \ "condition" \ "password").asOpt[String].map(x => x).getOrElse(""))
        )
    }

    def authWithPassword(func: JsValue => DBObject,
                         func_out: DBObject => Map[String, JsValue])
                        (data: JsValue)(db_name: String)
                        (implicit cm: CommonModules): Map[String, JsValue] = {

        val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("client").get

        db.queryObject(func(data), db_name)(func_out) match {
            case None => throw new Exception("email or password error")
            case Some(one) => one
        }
    }

    def authSetExpire(data: JsValue)
                     (implicit cm: CommonModules): Map[String, JsValue] = {

        val rd = cm.modules.get.get("rd").map(x => x.asInstanceOf[PhRedisDriverImpl]).getOrElse(throw new Exception("no redis connection"))
        val expire = (data \ "condition" \ "token_expire").asOpt[Int].map(x => x).getOrElse(24 * 60 * 60) //default expire in 24h
        val uid = (data \ "user_id").asOpt[String].map(x => x).get
        val email = (data \ "email").asOpt[String].map(x => x).get
        val accessToken = "bearer" + Sercurity.md5Hash(email + new Date().getTime)
        data.asOpt[Map[String, JsValue]].get
                .foreach { x =>
                    rd.addMap(accessToken, x._1, x._2.asOpt[String].getOrElse("null"))
                }
        rd.expire(accessToken, expire)

        Map(
            "uid" -> toJson(uid),
            "user_token" -> toJson(accessToken)
        )
    }

}
