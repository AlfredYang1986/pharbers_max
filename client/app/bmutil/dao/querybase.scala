/**
 * For Database Query
 * Created By Alfred Yang
 */

package bmutil.dao

import com.mongodb.casbah.Imports._

object _data_connection {
	def conn_name : String = "baby_new_2"

//	val addr = new com.mongodb.casbah.Imports.ServerAddress("localhost", 2017)
//	val credentialsList = MongoCredential.createPlainCredential("dongdamaster", conn_name, "dongda@master".toCharArray)
//    val _conn = MongoClient(addr, List(credentialsList))
	val _conn = MongoClient()

	var _conntion : Map[String, MongoCollection] = Map.empty
	
	def getCollection(coll_name : String) : MongoCollection = {
		if (!_conntion.contains(coll_name)) _conntion += (coll_name -> _conn(conn_name)(coll_name))
		
		_conntion.get(coll_name).get
	}
	
	def resetCollection(coll_name : String) : Unit = getCollection(coll_name).drop
	
	def isExisted(coll_name : String) : Boolean = !(getCollection(coll_name).isEmpty)
	
	def releaseConntions = _conntion = Map.empty
}

trait IDatabaseContext {
	var coll_name : String = null

	protected def openConnection : MongoCollection = 
	  	_data_connection._conn(_data_connection.conn_name)(coll_name)
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
	
	def select[U](cr: (MongoDBObject) => U) : IQueryable[U] = {
	 
		val mongoColl = openConnection
		val ct = mongoColl.find(w)
		var nc = new Linq_List[U]
		for (i <- ct) {
			nc = (nc :+ cr(i)).asInstanceOf[Linq_List[U]]
		}
		nc
	}

	def contains : Boolean = {
		!(select (x => x).empty)
	}
	
	def selectTop[U](n : Int)(o : String)(cr : (MongoDBObject) => U) : IQueryable[U] = {
		val mongoColl = openConnection
		val ct = mongoColl.find(w).sort(MongoDBObject(o -> -1)).limit(n)
		var nc = new Linq_List[U]
		for (i <- ct) {
			nc = (nc :+ cr(i)).asInstanceOf[Linq_List[U]]
		}
		nc
	}
	
	def selectSkipTop[U](skip : Int)(take : Int)(o : String)(cr : (MongoDBObject) => U) : IQueryable[U] = {
		val mongoColl = openConnection
		val ct = mongoColl.find(w).sort(MongoDBObject(o -> -1)).skip(skip).limit(take)
		var nc = new Linq_List[U]
		for (i <- ct) {
			nc = (nc :+ cr(i)).asInstanceOf[Linq_List[U]]
		}
		nc
	}
	
	def selectSkipTopLoc[U](skip : Int)(take : Int)(cr : (MongoDBObject) => U) : IQueryable[U] = {
		val mongoColl = openConnection
		val ct = mongoColl.find(w).skip(skip).limit(take)
		var nc = new Linq_List[U]
		for (i <- ct) {
			nc = (nc :+ cr(i)).asInstanceOf[Linq_List[U]]
		}
		nc
	}

	def selectCursor : MongoCursor = openConnection.find(w)

    /**
      * TODO: 后期需要优化
      */
    def aggregate(group : MongoDBObject) : DBObject = {
        val pipeline = MongoDBList(MongoDBObject("$match" -> w)) ++
                        MongoDBList(MongoDBObject("$group" -> group))

        val a = _data_connection._conn(_data_connection.conn_name)
        a.command(MongoDBObject("aggregate" -> coll_name, "pipeline" -> pipeline))
    }

	def count : Int = openConnection.count(w)
}
