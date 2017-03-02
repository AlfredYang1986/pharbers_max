package com.pharbers.aqll.db

import com.pharbers.aqll.db.shellcmd._


object ProformanceProtocol extends App {
    val t1 = dbdumpCmd("Max_Cores", "Test1")
    val t2 = dbdumpCmd("Max_Cores2", "Test1")

    t1.excute
    t2.excute

    val t3 = dbrestoreCmd("Max_Cores3", "Test1", "Max_Cores")
    val t4 = dbrestoreCmd("Max_Cores3", "Test1", "Max_Cores2")

    t3.excute
    t4.excute
}
