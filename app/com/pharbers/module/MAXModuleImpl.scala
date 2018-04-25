package com.pharbers.module

import akka.actor.ActorSystem
import org.bson.types.ObjectId
import javax.inject.{Inject, Singleton}
import com.pharbers.channel.msgChannel
import com.pharbers.driver.PhRedisDriverImpl
import com.pharbers.token.tokenImpl.TokenImplTrait
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.driver.util.redis_conn_cache

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
class MAXMsgChannelModule @Inject()(as_inject: ActorSystem) extends msgChannel(ObjectId.get().toString)(as_inject)
