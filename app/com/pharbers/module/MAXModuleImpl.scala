package com.pharbers.module

import javax.inject.Singleton
import com.pharbers.token.tokenImpl.TokenImplTrait
import com.pharbers.dbManagerTrait.dbInstanceManager

/**
  * Created by alfredyang on 01/06/2017.
  */
@Singleton
class MAXDBManager extends dbInstanceManager

@Singleton
class MAXTokenInjectModule extends TokenImplTrait
