package com.pharbers.aqll.test

import com.pharbers.aqll.db.shellcmd._


object ProformanceProtocol extends App {
    val t1 = dbdumpCmd("db_prefromance_test1", "prefromance")
    val t2 = dbdumpCmd("db_prefromance_test2", "prefromance")

    t1.excute
    t2.excute

    val t3 = dbrestoreCmd("db_prefromance_test3", "prefromance", "db_prefromance_test1")
    val t4 = dbrestoreCmd("db_prefromance_test3", "prefromance", "db_prefromance_test2")

    t3.excute
    t4.excute
}
