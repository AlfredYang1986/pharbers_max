package com.pharbers.aqll.common.MaxEnmeration

/**
  * Created by apple on 9/14/17.
  */
object alRegisterStatus extends Enumeration{
    type RegisterStatus = Value
    val posted, canceled, approved, rejected = Value
}

object alUserScope extends Enumeration{
    type UserScope = Value
    val AD, BD, NCA, NC = Value
}