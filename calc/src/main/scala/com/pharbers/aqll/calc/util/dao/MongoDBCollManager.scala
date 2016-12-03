package com.pharbers.util.dao

import com.mongodb.casbah.Imports._

object MongoDBCollManager { 
  
    /**
     * for further use which database and the service are not in the same machine
     */
    private def DBHost = "localhost"
    private def DBPort = ""
    private def username = ""
    private def password = ""

    /**
     * functional functions
     */
    private def getDefaultCilent : MongoClient = MongoClient()
    private def getBabyDatabase(name : String = "Max_Cores") : MongoDB = 
      	if (db == null) { db = this.getDefaultCilent(name); db }
      	else db
    
    private def getCollection(name : String) : MongoCollection = this.getBabyDatabase()(name)

    private def getProductsCollection : MongoCollection = this.getCollection("products")
//    private def getProductsDetailCollection : MongoCollection = this.getCollection("products_details")
    
    /**
     * nonfunctional functions, for control the total collections
     * in order to save bits
     */
   
    private var colls : Map[String, MongoCollection] = Map.empty
    private var db : MongoDB = null
    
    def getCollectionSafe(name : String) : MongoCollection = colls.get(name).getOrElse(null) match {
      case null => { val r = getCollection(name); colls += name -> r; r }
      case e : MongoCollection => {
          if (e.isTraversableAgain) e
          else { val r = getCollection(name); colls += name -> r; r }
      }
    }
    
    def isCollectionExist(name : String) : Boolean = this.getBabyDatabase().collectionExists(name)
    def removeCollection(name : String) = if (this.isCollectionExist(name)) {
    			this.getCollectionSafe(name).drop
    			colls = colls - name
      	}
    
    def insert(name : String)(obj : MongoDBObject) = this.getCollectionSafe(name) += obj
    
    def enumCollections : List[String] = this.getBabyDatabase().collectionNames.toList
}
