package com.pharbers.aqll.common.alCmd.dbcmd

import com.pharbers.aqll.common.alCmd.alCallShellCmdExce.otherFactory

case class dbdumpCmd(db: String,
                     coll: String,
                     out: String,
                     userName: String,
                     userPassword: String,
                     ip: String= "127.0.0.1",
                     port: Int = 27017) {
    val cmd = "/usr/bin/mongodump " + DBConfig(db, coll, out, Some(ip), Some(port), Some(userName), Some(userPassword)).toArgs
    otherFactory.CreateShellCmdExce().excute(cmd)
}