package com.pharbers.aqll.alCalcHelp.alFinalDataProcess

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.alCalcHelp.dbcores._
import com.pharbers.aqll.alStart.alHttpFunc.alCommitItem
import com.pharbers.driver.redis.phRedisDriver
import com.redis.RedisClient
import java.util.Date
import com.pharbers.aqll.alCalcHelp.alWebSocket.phWebSocket

case class alCollectionDictionary(item: alCommitItem) {

    val rd: RedisClient =  phRedisDriver().commonDriver
    val uid = item.uid
    val showLst = item.showLst

    def init: Boolean = {
        val company = rd.hget(uid, "company").map(x=>x).getOrElse(throw new Exception("not found company"))
        val rid = rd.hget(uid, "rid").map(x => x).getOrElse(throw new Exception("not found uid"))
        val tidDetails = rd.smembers(rid).get.map(x =>(rd.hget(x.get, "ym").get, rd.hget(x.get, "mkt").get, rd.hget(x.get, "tid").get))
        val msg = Map(
            "type" -> "progress_calc_result_done",
            "txt" -> "正在合并",
            "progress" -> "10"
        )
        phWebSocket(uid).post(msg)
        if (showLst.isEmpty) {
            tidDetails.foreach(x => {
                val bool = putNewItem(s"${company}_dictionary", x._1, x._2, s"${company}${x._3}")
                if(!bool){
                    postMsg(bool)
                    return bool
                }
            })
            postMsg(true)
            true
        } else {
            showLst.map( x => tidDetails.find(f => f._1 == x.split("-")(1) && f._2 == x.split("-")(0))).filterNot(_.isEmpty).foreach( x => {
                val bool = putNewItem(s"${company}_dictionary", x.get._1, x.get._2, s"${company}${x.get._3}")
                if(!bool){
                    postMsg(bool)
                    return bool
                }
            })
            postMsg(true)
            true
        }
    }

    def postMsg(result: Boolean) = {
        if (result) {
            val msg = Map(
                "type" -> "progress_calc_result_done",
                "txt" -> s"合并结束",
                "progress" -> "100"
            )
            phWebSocket(uid).post(msg)
        } else {
            val msg = Map(
                "type" -> "error",
                "error" -> "cannot generate CollectionDictionary data"
            )
            phWebSocket(uid).post(msg)
        }
    }

    def putNewItem(dictionaryColl: String, ym: String, market: String, tempColl: String): Boolean = {
		try {
			if(dbc.getCollection(dictionaryColl).count() == 0){
				openIndex(dictionaryColl)
			}
			val obj = dbc.getCollection(dictionaryColl).find($and(DBObject("ym" -> s"${ym}", "market" -> s"${market}")))

            if (obj.isEmpty) insertData(dictionaryColl, ym, market, tempColl)
            else appendData(obj.toList.head, dictionaryColl, ym, market, tempColl)

			true
		} catch {
			case ex: Exception =>
				println(s".....>> ${ex.getMessage}")
				false
		}
	}

	private def insertData(dictionaryColl: String, ym: String, market: String, tempColl: String) = {
		val builder = MongoDBObject.newBuilder
		builder += "ym" -> ym
		builder += "market" -> market
		builder += "collectionName" -> tempColl
		builder += "tbc" -> List.empty
		dbc.getCollection(dictionaryColl) += builder.result()
	}

    private def appendData(oldObj: DBObject, dictionaryColl: String, ym: String, market: String, tempColl: String): Unit ={

        val builder = MongoDBObject.newBuilder
        val objID = new ObjectId()
        builder += "_id" -> objID
        builder += "collectionName" -> oldObj.get("collectionName").asInstanceOf[String]
        dbc.getCollection(dictionaryColl) += builder.result()

        val newObj = DBObject("ym" -> s"${ym}", "market" -> s"${market}",
            "collectionName" -> s"${tempColl}", "tbc" -> oldObj.getAs[MongoDBList]("tbc").get.toList.+:(objID))
        dbc.getCollection(dictionaryColl).update(oldObj, newObj)
    }

	private def openIndex(dictionaryColl: String) ={
		dbc.getCollection(dictionaryColl).createIndex(MongoDBObject("ym" -> 1))
		dbc.getCollection(dictionaryColl).createIndex(MongoDBObject("market" -> 1))
		dbc.getCollection(dictionaryColl).createIndex(MongoDBObject("ym" -> 1, "market" -> 1))
	}

}
