/**
  * For Database Query
  * Created By Alfred Yang
  */

package com.pharbers.aqll.common.alDao

import com.mongodb.casbah.Imports.{MongoDBObject, _}
import com.mongodb.casbah.{MongoClient, MongoCollection}
import com.mongodb.{DBObject, MongoCredential}

import scala.concurrent.stm.{Ref, atomic}

trait data_connection {
    def conn_name : String
    def addr: ServerAddress
    def credentialsList: MongoCredential

    val _conn = MongoClient(addr, List(credentialsList))

    var _conntion : Map[String, MongoCollection] = Map.empty
    def getCollection(coll_name : String) : MongoCollection = {
        if (!_conntion.contains(coll_name)) _conntion += (coll_name -> _conn(conn_name)(coll_name))

        _conntion.get(coll_name).get
    }

    def resetCollection(coll_name : String) : Unit = getCollection(coll_name).drop

    def isExisted(coll_name : String) : Boolean = !(getCollection(coll_name).isEmpty)

    def releaseConntions = _conntion = Map.empty
}

object dataFactory {

    def getDataCores(host: String = "127.0.0.1", port: Int = 27017, user: String = "", pwd: String = "", dbname: String = "Max_Cores"): data_connection = new DataConnection(host, port, user, pwd, dbname)

    def getDataBasic(host: String = "127.0.0.1", port: Int = 27017, user: String = "", pwd: String = "", dbname: String = "Max_Basic"): data_connection = new DataConnection(host, port, user, pwd, dbname)
}

class DataConnection(host: String, port: Int, user: String, pwd: String, dbname: String) extends data_connection {
    override def conn_name: String = dbname
    override def addr: ServerAddress = new ServerAddress(host, port)
    override def credentialsList: MongoCredential = MongoCredential.createScramSha1Credential(user, conn_name , pwd.toCharArray)
}

object _data_connection_cores_thread extends data_connection {
    override def conn_name: String = "Max_Cores"

    override def addr: ServerAddress = new ServerAddress("127.0.0.1",2017)
    override def credentialsList: MongoCredential = MongoCredential.createScramSha1Credential("Pharbers", conn_name ,"Pharbers2017.".toCharArray)

    var conntion  = Ref(Map[String , MongoCollection]().empty)

    override def getCollection(coll_name : String) : MongoCollection = {
        atomic { implicit thx =>
            if (!conntion.single.get.contains(coll_name)){
                conntion() = conntion() + (coll_name -> _conn(conn_name).apply(coll_name))
            }
            conntion.single.get.get(coll_name).get
        }
    }

    override def releaseConntions = {
        atomic { implicit thx =>
            conntion() = Map.empty
        }
    }
}

trait IDatabaseContext {
    var coll_name : String = null
    protected def openConnection(implicit dbc: data_connection) : MongoCollection = dbc._conn(dbc.conn_name)(coll_name)
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
    var w2 : List[DBObject] = List.empty

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
                case a: List[DBObject] => w2 = w2 ++: a
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

    def selectSort[U](o : String)(cr: (MongoDBObject) => U)(implicit dbc: data_connection) : IQueryable[U] = {
        val mongoColl = openConnection
        val ct = mongoColl.find(w).sort(MongoDBObject(o -> 1))
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

    def selectOneByOne[U](o: String)(cr: (MongoDBObject) => U)(implicit dbc: data_connection): MongoCursor = {
        val mongoColl = openConnection
        mongoColl.find(w).sort(MongoDBObject(o -> -1))
    }

    def selectAggregate[U](cr: (MongoDBObject) => U)(implicit dbc: data_connection) : IQueryable[U] = {
        val mongoColl = openConnection
        val aggregationOptions = AggregationOptions(AggregationOptions.CURSOR)
        val ct = mongoColl.aggregate(w2, aggregationOptions)
        var nc = new Linq_List[U]
        for (i <- ct) {
            nc = (nc :+ cr(i)).asInstanceOf[Linq_List[U]]
        }
        nc
    }

    def selectCursor[U](cr: (com.mongodb.casbah.commons.MongoDBObject) => U)(implicit dbc: data_connection) : MongoCursor = {
        val mongoColl = openConnection
        mongoColl.find(w)
    }

    def count(implicit dbc: data_connection) : Int = openConnection.count(w)
}