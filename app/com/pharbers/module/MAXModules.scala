package com.pharbers.module

import com.pharbers.token.AuthTokenTrait
import play.api.{Configuration, Environment}
import com.pharbers.driver.PhRedisDriverImpl
import com.pharbers.dbManagerTrait.dbInstanceManager

/**
  * Created by qianpeng on 2017/9/19.
  */
class MAXModules extends play.api.inject.Module {
    def bindings(env : Environment, conf : Configuration) = {
        Seq(
            bind[dbInstanceManager].to[MAXDBManager],
            bind[AuthTokenTrait].to[MAXTokenInjectModule],
            bind[PhRedisDriverImpl].to[MAXRedisManager],
            bind[MAXCallJobPusher].toSelf,
            bind[MAXResponseConsumer].toSelf,
            bind[MAXSearchFacade].toSelf
        )
    }
}

