package com.pharbers.module

import akka.actor.ActorSystem
import org.bson.types.ObjectId
import javax.inject.{Inject, Singleton}
import com.pharbers.token.AuthTokenTrait
import com.pharbers.bmmessages.CommonModules
import com.pharbers.driver.PhRedisDriverImpl
import com.pharbers.driver.util.redis_conn_cache
import com.pharbers.token.tokenImpl.TokenImplTrait
import com.pharbers.dbManagerTrait.dbInstanceManager
import module.jobs.channel.{callJobPusher, progressConsumer}

/**
  * Created by alfredyang on 01/06/2017.
  */
@Singleton
class MAXDBManager extends dbInstanceManager

@Singleton
class MAXTokenInjectModule extends TokenImplTrait

@Singleton
class MAXRedisManager extends redis_conn_cache with PhRedisDriverImpl

@Singleton
class MAXCallJobPusher extends callJobPusher

@Singleton
class MAXProgressConsumer @Inject()(as_inject: ActorSystem, dbt: dbInstanceManager) extends progressConsumer(as_inject, dbt)

