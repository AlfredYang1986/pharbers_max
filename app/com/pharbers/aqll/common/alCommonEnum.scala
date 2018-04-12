package com.pharbers.aqll.common

/**
  * Created by liwei on 2017/5/22.
  */
object alCommonEnum extends Enumeration {
    type alCommonEnum = Value
    val AverageUser = Value(0, "普通用户")
    val Administrators = Value(1, "管理员")
    val SuperAdministrator = Value(2, "超级管理员")
}
