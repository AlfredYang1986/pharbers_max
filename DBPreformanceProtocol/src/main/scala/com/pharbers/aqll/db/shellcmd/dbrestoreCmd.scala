package com.pharbers.aqll.db.shellcmd

case class dbrestoreCmd(val db : String, val coll : String, val dir : String) extends shellCmdExce {
    val cmd = "/usr/local/mongodb/bin/mongorestore " + DBInputConfig(db, coll, s"/Users/BM/Desktop/test/$dir").toArgs
}
