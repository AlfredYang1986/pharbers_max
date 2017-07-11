package com.pharbers.aqll.dbmodule

import com.pharbers.aqll.common.alDao.dataFactory._
import com.pharbers.aqll.common.alFileHandler.databaseConfig._
import play.api.inject.Binding
import play.api.{Configuration, Environment}
import play.api.inject.Module

/**
  * Created by qianpeng on 2017/5/19.
  */
class MongoModule extends Module {
    override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] =
        Seq(
            bind[MongoDBModule].toSelf
        )
}

class MongoDBModule {
    def cores = getDataCores(dbhost, dbport.toInt, dbuser, dbpwd, db1)
    
    def basic = getDataBasic(dbhost, dbport.toInt, dbuser, dbpwd, db2)
}
