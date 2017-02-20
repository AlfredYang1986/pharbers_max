package com.pharbers.aqll.calc.maxresult

import com.mongodb.casbah.commons.{MongoDBList, MongoDBObject}
import com.pharbers.aqll.calc.util.dao._data_connection

import com.pharbers.aqll.calc.util.dao.from

import scala.collection.mutable.ArrayBuffer
import com.mongodb.casbah.Imports._

class Insert {

	def maxResultInsert(mr: List[(String, (Long, Double, Double, ArrayBuffer[(String)], ArrayBuffer[(String)], ArrayBuffer[(String)], String, ArrayBuffer[(String)], ArrayBuffer[String], ArrayBuffer[String], ArrayBuffer[String]))])(m: (String, String, String, Long)) = {
		def maxInser() = {
			_data_connection.getCollection(m._2).createIndex(MongoDBObject("Date" -> 1))

			val bulk = _data_connection.getCollection(m._2).initializeUnorderedBulkOperation

			mr.toList.filterNot(x => x._2._2 == 0 && x._2._3 == 0).groupBy(z => (z._2._4.head, z._2._8.head, z._2._5.head, z._2._1)).foreach { x =>
				bulk.insert(Map("ID" -> m._3,
					"f_units" -> x._2.map(_._2._3).sum,
					"f_sales" -> x._2.map(_._2._2).sum,
					"Panel_ID" -> x._1._1,
					"Product" -> x._1._3,
					"City" -> x._1._2,
					"Date" -> x._1._4))
			}
			bulk.execute()
		}

		//      println(s"mr.toList.size = ${mr.toList.filterNot(x => x._2._2 == 0 && x._2._3 == 0).size}")
		//
		//      println(s"aaa111.sum = ${mr.toList.filter(_._2._9.head.equals("1")).map(_._2._2).sum}")
		//      println(s"bbb111.sum = ${mr.toList.filter(_._2._9.head.equals("1")).map(_._2._3).sum}")
		//
		//      println(s"aaa000.sum = ${mr.toList.filter(_._2._9.head.equals("0")).map(_._2._2).sum}")
		//      println(s"bbb000.sum = ${mr.toList.filter(_._2._9.head.equals("0")).map(_._2._3).sum}")
		//
		      println(s"mr.toList.map(_._2._1).sum = ${mr.toList.map(_._2._2).sum}")
		      println(s"mr.toList.map(_._2._2).sum = ${mr.toList.map(_._2._3).sum}")
		println(s"m._2 = ${m._2}")

		val conditions = ("ID" -> m._3)
		val count = (from db() in m._2 where conditions count)
		println(s"count = ${count}")
		count match {
			case 0 => {
				maxInser()
			}

			case _ => {
				val rm = MongoDBObject(conditions)
				_data_connection.getCollection(m._2).remove(rm)
				maxInser()
			}
		}
	}

	def maxFactResultInsert(model: (Double, Double, Int, List[String], List[String], List[String]))(m: (String, String, String, Long)) = {
		def maxInser() = {
			val builder = MongoDBObject.newBuilder
			builder += "ID" -> m._3
			builder += "CompanyID" -> m._2
			builder += "Units" -> model._2
			builder += "Sales" -> model._1
			builder += "HospitalNum" -> model._3
			builder += "ProductMinuntNum" -> model._4.size
			val lsth_builder = MongoDBList.newBuilder
			model._6 foreach (lsth_builder += _)
			val lstm_builder = MongoDBList.newBuilder
			model._4 foreach (lstm_builder += _)

			builder += "Condition" -> Map("Hospital" -> lsth_builder.result, "ProductMinunt" -> lstm_builder.result)
			builder += "Timestamp" -> m._4
			builder += "Filepath" -> m._1.substring(m._1.lastIndexOf("\\") + 1, m._1.length)
			_data_connection.getCollection("FactResult") += builder.result
		}

		val conditions = ("ID" -> m._3)
		val count = (from db() in "FactResult" where conditions count)
		count match {
			case 0 => {
				maxInser()
			}

			case _ => {
				val rm = MongoDBObject(conditions)
				_data_connection.getCollection("FactResult").remove(rm)
				maxInser()
			}
		}
	}
}