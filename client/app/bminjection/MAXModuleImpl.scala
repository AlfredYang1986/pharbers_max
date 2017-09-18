package bminjection

import javax.inject.Singleton

import bminjection.db.MongoDB.MongoDBImpl

/**
  * Created by alfredyang on 01/06/2017.
  */
@Singleton
class MAXModuleImpl extends  MongoDBImpl
