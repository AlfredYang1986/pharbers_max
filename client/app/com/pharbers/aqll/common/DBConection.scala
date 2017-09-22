package com.pharbers.aqll.common

import com.pharbers.dbManagerTrait.dbInstanceManager

object DBConection extends dbInstanceManager{
	config
	val basic = queryDBInstance("cli").get
	val cores = queryDBInstance("calc").get
}
