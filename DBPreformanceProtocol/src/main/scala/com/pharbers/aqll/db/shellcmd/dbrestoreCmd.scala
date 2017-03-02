package com.pharbers.aqll.db.shellcmd

case class dbrestoreCmd(val db : String, val coll : String, val dir : String) extends shellCmdExce {
    val cmd = "/usr/local/bin/mongorestore " +
        DBInputConfig(db, coll, s"/Users/qianpeng/Desktop/test/$dir", Some("127.0.0.1"), Some(2017), Some("Pharbers"), Some("Pharbers2017.")).toArgs
}
