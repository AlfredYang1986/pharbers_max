package com.pharbers.aqll.calc.util.mongo.shellcmd

case class dbrestoreCmd(val db : String, val coll : String, val dir : String) extends shellCmdExce {
    val cmd = "/usr/bin/mongorestore " +
        DBInputConfig(db, coll, s"/root/program/scpdb/$dir", Some("59.110.31.215"), Some(2017), Some("Pharbers"), Some("Pharbers2017.")).toArgs
}
