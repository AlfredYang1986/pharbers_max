package com.pharbers.aqll.db.shellcmd

case class dbdumpCmd(val db : String, val coll : String) extends shellCmdExce {
    val cmd = "/usr/local/mongodb/bin/mongodump " + DBConfig(db, coll, "/Users/BM/Desktop/test").toArgs
}
