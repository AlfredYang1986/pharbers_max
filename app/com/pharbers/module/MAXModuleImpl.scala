package com.pharbers.module

import akka.actor.ActorSystem
import com.pharbers.builder.SearchFacade
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.driver.PhRedisDriverImpl
import com.pharbers.driver.util.redis_conn_cache
import com.pharbers.token.tokenImpl.TokenImplTrait
import javax.inject.{Inject, Singleton}
import module.jobs.callJob.{callJobPusher, responseConsumer}

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
class MAXResponseConsumer @Inject()(as_inject: ActorSystem, dbt: dbInstanceManager) extends responseConsumer(as_inject, dbt)

@Singleton
class MAXSearchFacade extends SearchFacade

