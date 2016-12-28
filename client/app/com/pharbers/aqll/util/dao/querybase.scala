/**
 * For Database Query
 * Created By Alfred Yang
 */

package com.pharbers.aqll.util.dao 

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.query.dsl.QueryExpressionObject

trait data_connection {
    def conn_name : String
    val _conn = MongoConnection()
	var _conntion : Map[String, MongoCollection] = Map.empty
	
	def getCollection(coll_name : String) : MongoCollection = {
		if (!_conntion.contains(coll_name)) _conntion += (coll_name -> _conn(conn_name)(coll_name))
		
		_conntion.get(coll_name).get
	}
	
	def resetCollection(coll_name : String) : Unit = getCollection(coll_name).drop
	
	def isExisted(coll_name : String) : Boolean = !(getCollection(coll_name).isEmpty)
	
	def releaseConntions = _conntion = Map.empty
}

object _data_connection_cores extends data_connection {
    
    override def conn_name: String = "Max_Cores" 
}

object _data_connection_basic extends data_connection {
    
    override def conn_name: String = "Max_Basic"
}

//object _data_connection {
//    def conn_name : String = "Max_Cores"
//    
//	val _conn = MongoConnection()
//	
//	var _conntion : Map[String, MongoCollection] = Map.empty
//	
//	def getCollection(coll_name : String) : MongoCollection = {
//		if (!_conntion.contains(coll_name)) _conntion += (coll_name -> _conn(conn_name)(coll_name))
//		
//		_conntion.get(coll_name).get
//	}
//	
//	def resetCollection(coll_name : String) : Unit = getCollection(coll_name).drop
//	
//	def isExisted(coll_name : String) : Boolean = !(getCollection(coll_name).isEmpty)
//	
//	def releaseConntions = _conntion = Map.empty
//}

trait IDatabaseContext {
	var coll_name : String = null
	
	protected def openConnection(implicit dbc: data_connection) : MongoCollection = 
	    dbc._conn(dbc.conn_name)(coll_name)
//	    new _data_connection2()._conn(new _data_connection2().conn_name)(coll_name)
//	  	_data_connection._conn(_data_connection.conn_name)(coll_name)
	    
	protected def closeConnection = null
}

class ALINQ[T] {
	var w : T => Boolean = x => true
	var ls : List[T] = Nil
  
	def in(l: List[T]) : ALINQ[T] = {
		ls = l
		this
	}

	def where(f: T => Boolean) : ALINQ[T] = {
		w = f
		this
	}
	
	def select[U](cr: (T) => U) : IQueryable[U] = {
		var nc = new Linq_List[U]
		for (i <- ls) {
			if (w(i)) nc = (nc :+ cr(i)).asInstanceOf[Linq_List[U]]
		}
		nc
	}
	
	def contains : Boolean = {
		for (i <- ls) {
			if (w(i)) true
		}
		false
	}
	
	def count : Int = ls.count(w)
}

object from {
	def apply[T]() : ALINQ[T] = new ALINQ[T]
	def db() : AMongoDBLINQ = new AMongoDBLINQ
}

class AMongoDBLINQ extends IDatabaseContext {
	var w : DBObject = null
  
	def in(l: String) : AMongoDBLINQ = {
		coll_name = l
		this
	}

	def where(args: Any* ) : AMongoDBLINQ = {
		w = new MongoDBObject
		for (arg <- args) {
			arg match {
			  case a: (String, AnyRef) => w += a
			  case a: DBObject => w = w ++ a
			  case _ => w
			}
		}
		this
	}
	
	def select[U](cr: (MongoDBObject) => U)(implicit dbc: data_connection) : IQueryable[U] = {
		val mongoColl = openConnection
		val ct = mongoColl.find(w)
		var nc = new Linq_List[U]
		for (i <- ct) {
			nc = (nc :+ cr(i)).asInstanceOf[Linq_List[U]]
		}
		nc
	}

	def contains(implicit dbc: data_connection) : Boolean = {
		!(select (x => x).empty)
	}
	
	def selectTop[U](n : Int)(o : String)(cr : (MongoDBObject) => U)(implicit dbc: data_connection) : IQueryable[U] = {
		val mongoColl = openConnection
		val ct = mongoColl.find(w).sort(MongoDBObject(o -> -1)).limit(n)
		var nc = new Linq_List[U]
		for (i <- ct) {
			nc = (nc :+ cr(i)).asInstanceOf[Linq_List[U]]
		}
		nc
	}
	
	def selectSkipTop[U](skip : Int)(take : Int)(o : String)(cr : (MongoDBObject) => U)(implicit dbc: data_connection) : IQueryable[U] = {
		val mongoColl = openConnection
		val ct = mongoColl.find(w).sort(MongoDBObject(o -> -1)).skip(skip).limit(take)
		var nc = new Linq_List[U]
		for (i <- ct) {
			nc = (nc :+ cr(i)).asInstanceOf[Linq_List[U]]
		}
		nc
	}

	def count(implicit dbc: data_connection) : Int = openConnection.count(w)
}
