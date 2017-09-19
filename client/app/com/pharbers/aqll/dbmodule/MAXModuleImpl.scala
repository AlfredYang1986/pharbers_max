package com.pharbers.aqll.dbmodule

import javax.inject.Singleton

import com.pharbers.aqll.dbmodule.db.MongoDB.MongoDBImpl
import com.pharbers.token.tokenImpl.TokenImplTrait



/**
  * Created by alfredyang on 01/06/2017.
  */
@Singleton
class MAXModuleImpl extends TokenImplTrait with MongoDBImpl