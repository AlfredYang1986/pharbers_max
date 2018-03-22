package com.pharbers.aqll.alCalcHelp.alFinalDataProcess

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.alCalcHelp.dbcores._

case class alCollectionDictionary(dictionaryColl: String, ym: String, market: String, tempColl: String) {
	def putNewItem: Boolean = {
		try {
			if(dbc.getCollection(dictionaryColl).count() == 0){
				openIndex
			}
			val obj = dbc.getCollection(dictionaryColl).find($and(DBObject("ym" -> s"${ym}", "market" -> s"${market}")))
			obj.isEmpty match {
				case true => insertData
				case false => appendData(obj.toList.head)
			}

			true
		} catch {
			case ex: Exception =>
				println(s".....>> ${ex.getMessage}")
				false
		}
	}

	private def insertData = {
		val builder = MongoDBObject.newBuilder
		builder += "ym" -> ym
		builder += "market" -> market
		builder += "collectionName" -> tempColl
		builder += "tbc" -> List.empty
		dbc.getCollection(dictionaryColl) += builder.result()
	}

    private def appendData(oldObj: DBObject): Unit ={

        val builder = MongoDBObject.newBuilder
        val objID = new ObjectId()
        builder += "_id" -> objID
        builder += "collectionName" -> oldObj.get("collectionName").asInstanceOf[String]
        dbc.getCollection(dictionaryColl) += builder.result()

        val newObj = DBObject("ym" -> s"${ym}", "market" -> s"${market}",
            "collectionName" -> s"${tempColl}", "tbc" -> oldObj.getAs[MongoDBList]("tbc").get.toList.+:(objID))
        dbc.getCollection(dictionaryColl).update(oldObj, newObj)
    }

	private def openIndex ={
		dbc.getCollection(dictionaryColl).createIndex(MongoDBObject("ym" -> 1))
		dbc.getCollection(dictionaryColl).createIndex(MongoDBObject("market" -> 1))
		dbc.getCollection(dictionaryColl).createIndex(MongoDBObject("ym" -> 1, "market" -> 1))
	}

}
