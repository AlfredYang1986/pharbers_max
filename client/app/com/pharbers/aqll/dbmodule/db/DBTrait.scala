package com.pharbers.aqll.dbmodule.db

import com.mongodb.casbah.Imports._
import com.pharbers.aqll.common.alDao.data_connection
import play.api.libs.json.JsValue

/**
  * Created by alfredyang on 01/06/2017.
  */
trait DBTrait {
    def insertObject(obj : DBObject, db_name : String, primary_key : String)(implicit connection: data_connection) : Unit
    def updateObject(obj : DBObject, db_name : String, primary_key : String)(implicit connection: data_connection) : Unit

    def queryObject(condition : DBObject, db_name : String)
                   (implicit t : DBObject => Map[String, JsValue], connection: data_connection) : Option[Map[String, JsValue]]
    def queryMultipleObject(condition : DBObject, db_name : String, sort : String = "date", skip : Int = 0, take : Int = 20)
                           (implicit t : DBObject => Map[String, JsValue], connection: data_connection) : List[Map[String, JsValue]]

    def querySum(condition : DBObject, db_name : String)
                (sum : (Map[String, JsValue], Map[String, JsValue]) => Map[String, JsValue])
                (acc: (DBObject) => Map[String, JsValue])
                (implicit connection: data_connection): Option[Map[String, JsValue]]

    def aggregate(condition : DBObject, db_name : String, group : DBObject)
                 (implicit t : DBObject => Map[String, JsValue], connection: data_connection) : Option[Map[String, JsValue]]

    def deleteObject(obj : DBObject, db_name : String, primary_key : String)(implicit connection: data_connection) : Unit

    def restoreDatabase() = ???
    def dumpDatabase() = ???
}
