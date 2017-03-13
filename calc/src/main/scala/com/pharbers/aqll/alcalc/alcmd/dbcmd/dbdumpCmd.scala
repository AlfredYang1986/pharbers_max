package com.pharbers.aqll.alcalc.alcmd.dbcmd

import com.pharbers.aqll.alcalc.alcmd.shellCmdExce

case class dbdumpCmd(val db : String, val coll : String) extends shellCmdExce {
    val cmd = "/usr/bin/mongodump " +
        DBConfig(db, coll, "/root/program/scpdb", Some("127.0.0.1"), Some(2017), Some("Pharbers"), Some("Pharbers2017.")).toArgs
}
