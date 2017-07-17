package com.pharbers.aqll.common.alCmd.dbcmd

import com.pharbers.aqll.common.alCmd.alShellOtherCmdExce

case class dbrestoreCmd(db: String,
                        coll: String,
                        dir: String,
                        userName: String,
                        userPassword: String,
                        ip: String = "127.0.0.1",
                        port: Int = 27017) extends alShellOtherCmdExce {
   override def cmd = "/usr/bin/mongorestore " + DBInputConfig(db, coll, dir, Some(ip), Some(port), Some(userName), Some(userPassword)).toArgs
}