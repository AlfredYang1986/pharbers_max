package controllers

import javax.inject._

import com.mongodb.casbah.Imports.{BasicDBObject, MongoDBList}
import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.common.alDao.{dataFactory, from}
import com.pharbers.aqll.dbmodule.MongoDBModule
import com.pharbers.aqll.pattern.CommonModule
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, Controller}

/**
  * Created by qianpeng on 2017/5/19.
  */
class TestController @Inject() (mdb: MongoDBModule) extends Controller{
	implicit val dbc = mdb.basic

	implicit val cm = CommonModule(Some(Map("" -> None)))

	def Test = Action {
		val lst = (from db() in "Company").select(x => toJson(Map("E_Mail" -> toJson(x.getAs[String]("E-Mail").get)))).toList
		println(s"size = ${lst.size}")
		Ok(views.html.test("Your new application is ready."))
	}
}
