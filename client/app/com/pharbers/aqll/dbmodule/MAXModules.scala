package com.pharbers.aqll.dbmodule

import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.message.send.SendMessageTrait
import com.pharbers.token.AuthTokenTrait
import play.api.{Configuration, Environment}

/**
  * Created by qianpeng on 2017/9/19.
  */
class MAXModules extends play.api.inject.Module {
    def bindings(env : Environment, conf : Configuration) = {
        Seq(
            bind[dbInstanceManager].to[MAXDBManager],
            bind[AuthTokenTrait].to[MAXTokenInjectModule],
            bind[SendMessageTrait].to[MAXMessageInjectModule]
        )
    }
}

