package com.pharbers.module

import com.pharbers.token.AuthTokenTrait
import play.api.{Configuration, Environment}
import com.pharbers.dbManagerTrait.dbInstanceManager

/**
  * Created by qianpeng on 2017/9/19.
  */
class MAXModules extends play.api.inject.Module {
    def bindings(env : Environment, conf : Configuration) = {
        Seq(
            bind[dbInstanceManager].to[MAXDBManager],
            bind[AuthTokenTrait].to[MAXTokenInjectModule]
        )
    }
}

