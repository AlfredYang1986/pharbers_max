package com.pharbers.aqll.alcalc.aljobs

import java.util.UUID

/**
  * Created by Alfred on 10/03/2017.
  */
trait alJob {
    val uuid = UUID.randomUUID

    def init(args : Map[String, Any])
    def next
    def clean
}
