package com.pharbers.aqll.calc.maxresult

import com.mongodb.casbah.commons.{MongoDBList, MongoDBObject}
import com.pharbers.aqll.calc.util.dao._data_connection
import com.pharbers.aqll.calc.util.dao.from

import scala.collection.mutable.ArrayBuffer
import com.mongodb.casbah.Imports._
import com.pharbers.aqll.calc.util.MD5

class Insert {

	def maxResultInsert(mr: List[(String, (Long, Double, Double, ArrayBuffer[(String)], ArrayBuffer[(String)], ArrayBuffer[(String)], String, ArrayBuffer[(String)], ArrayBuffer[String], ArrayBuffer[String], ArrayBuffer[String]))])
	                   (m: (String, String, String, Long)): (String, String) = {
		def maxInser(): (String, String) = {
			_data_connection.getCollection(m._2).createIndex(MongoDBObject("Date" -> 1))
			_data_connection.getCollection(m._2).createIndex(MongoDBObject("Index" -> 1))
			_data_connection.getCollection(m._2 + "temp").createIndex(MongoDBObject("Index" -> 1))
			_data_connection.getCollection(m._2 + "temp").createIndex(MongoDBObject("ID" -> 1))
//			_data_connection.getCollection(m._2 + "Indextemp").createIndex(MongoDBObject("Index" -> 1))
//			_data_connection.getCollection(m._2 + "Indextemp").createIndex(MongoDBObject("ID" -> 1))

			val bulk = _data_connection.getCollection(m._2 + "temp").initializeUnorderedBulkOperation
//			val bulk2 = _data_connection.getCollection(m._2 + "Indextemp").initializeUnorderedBulkOperation
			mr.toList.filterNot(x => x._2._2 == 0 && x._2._3 == 0).groupBy(z => (z._2._4.head, z._2._8.head, z._2._5.head, z._2._1)).foreach { x =>
				//				val conditions = List("Panel_ID" $eq x._1._1, "Date" $eq x._1._4, "Product" $eq x._1._3, "City" $eq x._1._2)
				//				val conditions = List("Index" $eq MD5.md5(x._1._1 + x._1._4 + x._1._3 + x._1._2))
				//				(from db() in m._2 where $and(conditions)).select(x => x).toList match {
				//					case head :: Nil => {
				//						val units = (x._2.map(_._2._3).sum + head.find(_._1 == "f_units").get._2.asInstanceOf[Number].doubleValue()).asInstanceOf[Number]
				//						val sales = (x._2.map(_._2._2).sum + head.find(_._1 == "f_sales").get._2.asInstanceOf[Number].doubleValue()).asInstanceOf[Number]
				//						head += "f_units" -> units
				//						head += "f_sales" -> sales
				////						_data_connection.getCollection(m._2).update(DBObject("Panel_ID" -> x._1._1, "Date" -> x._1._4, "Product" -> x._1._3, "City" -> x._1._2), head)
				//						_data_connection.getCollection(m._2).update(DBObject("Index" -> MD5.md5(x._1._1 + x._1._4 + x._1._3 + x._1._2)), head)
				//					}
				//					case _ => {
				//						val builder = MongoDBObject.newBuilder
				//						builder += "f_units" -> x._2.map(_._2._3).sum
				//						builder += "f_sales" -> x._2.map(_._2._2).sum
				//						builder += "Panel_ID" -> x._1._1
				//						builder += "Product" -> x._1._3
				//						builder += "City" -> x._1._2
				//						builder += "Date" -> x._1._4
				//						builder += "Index" -> MD5.md5(x._1._1 + x._1._4 + x._1._3 + x._1._2)
				//						_data_connection.getCollection(m._2) += builder.result()
				//
				//					}
				//
				//				}
				bulk.insert(Map("ID" -> m._3,
					"f_units" -> x._2.map(_._2._3).sum,
					"f_sales" -> x._2.map(_._2._2).sum,
					"Panel_ID" -> x._1._1,
					"Product" -> x._1._3,
					"City" -> x._1._2,
					"Date" -> x._1._4,
					"Index" -> MD5.md5(x._1._1 + x._1._4 + x._1._3 + x._1._2)))
				//bulk2.insert(Map("ID" -> m._3, "Index" -> MD5.md5(x._1._1 + x._1._4 + x._1._3 + x._1._2)))
			}
			bulk.execute()
			//bulk2.execute()
			(m._3, m._2)
		}

		println(s"mr.toList.map(_._2._1).sum = ${mr.toList.map(_._2._2).sum}")
		println(s"mr.toList.map(_._2._2).sum = ${mr.toList.map(_._2._3).sum}")

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
				_data_connection.getCollection(m._2 + "temp").remove(rm)
				maxInser()
			}
		}
	}

	def groupByResutInsert(id: String, company: String) = {
		val conditions = ("ID" -> id)
//		val list = (from db() in company + "Indextemp" where conditions).selectOneByOne("Index")(x => x)
		val list = (from db() in company + "temp"  where conditions).selectOneByOne("Index")(x => x)
		var temp: DBObject = DBObject.empty
		val bulk = _data_connection.getCollection(company).initializeUnorderedBulkOperation
		var n = 0
		var f_units = 0.0
		var f_sales = 0.0
		list foreach { x =>
//			if(temp != x.get("Index").toString){
//				n += 1
//				val conditions = List("ID" $eq id, "Index" $eq x.get("Index").toString)
//				val lst = (from db() in (company + "temp") where $and(conditions)).select(x => x).toList
//				println(n)
//				bulk.insert(Map("ID" -> id,
//					"f_units" -> lst.map(_.get("f_units").get.asInstanceOf[Number].doubleValue()).sum,
//					"f_sales" -> lst.map(_.get("f_sales").get.asInstanceOf[Number].doubleValue()).sum,
//					"Panel_ID" -> lst.head.get("Panel_ID").toString,
//					"Product" -> lst.head.get("Product").toString,
//					"City" -> lst.head.get("City").toString,
//					"Date" -> lst.head.get("Date").toString,
//					"Index" -> x.get("Index").toString))
//			}
			if(!temp.isEmpty && temp.get("Index").toString == x.get("Index").toString) {
				f_units += x.get("f_units").asInstanceOf[Number].doubleValue()
				f_sales += x.get("f_sales").asInstanceOf[Number].doubleValue()
			}else{
				if(!temp.isEmpty && f_sales != 0.0) {
					bulk.insert(Map("ID" -> id,
						"f_units" -> f_units,
						"f_sales" -> f_sales,
						"Panel_ID" -> temp.get("Panel_ID").toString,
						"Product" -> temp.get("Product").toString,
						"City" -> temp.get("City").toString,
						"Date" -> temp.get("Date").asInstanceOf[Number].longValue(),
						"Index" -> temp.get("Index").toString))
				}else{
					bulk.insert(Map("ID" -> id,
						"f_units" -> x.get("f_units").asInstanceOf[Number].doubleValue(),
						"f_sales" -> x.get("f_sales").asInstanceOf[Number].doubleValue(),
						"Panel_ID" -> x.get("Panel_ID").toString,
						"Product" -> x.get("Product").toString,
						"City" -> x.get("City").toString,
						"Date" -> x.get("Date").asInstanceOf[Number].longValue(),
						"Index" -> x.get("Index").toString))
				}
				f_units = 0.0
				f_sales = 0.0
			}
			temp = x
		}
		bulk.execute()
	}


	def maxFactResultInsert(model: (Double, Double, Int, List[String], List[String], List[String], Long))(m: (String, String, String, Long)) = {
		def maxInser() = {
			val builder = MongoDBObject.newBuilder
			builder += "ID" -> m._3
			builder += "CompanyID" -> m._2
			builder += "Units" -> model._2
			builder += "Sales" -> model._1
			builder += "HospitalNum" -> model._3
			builder += "ProductMinuntNum" -> model._4.size
			builder += "MarketNum" -> model._5.size
			val lsth_builder = MongoDBList.newBuilder
			model._6 foreach (lsth_builder += _)
			val lstm_builder = MongoDBList.newBuilder
			model._4 foreach (lstm_builder += _)

			builder += "Condition" -> Map("Hospital" -> lsth_builder.result, "ProductMinunt" -> lstm_builder.result)
			builder += "Timestamp" -> m._4
			builder += "Date" -> model._7
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