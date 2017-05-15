package com.pharbers.aqll.common.alCmd.dbcmd

import com.pharbers.aqll.old.calc.alcalc.alcmd.shellCmdExce

case class dbdumpCmd(db: String,
                     coll: String,
                     out: String,
                     userName: String,
                     userPassword: String,
                     ip: String= "127.0.0.1",
                     port: Int = 27017) extends shellCmdExce {
    val cmd = "/usr/bin/mongodump " +
        DBConfig(db, coll, out, Some(ip), Some(port), Some(userName), Some(userPassword)).toArgs
}
