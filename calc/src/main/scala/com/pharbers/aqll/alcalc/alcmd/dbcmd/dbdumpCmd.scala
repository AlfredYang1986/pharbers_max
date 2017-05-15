package com.pharbers.aqll.alcalc.alcmd.dbcmd

import com.pharbers.aqll.alcalc.alcmd.shellCmdExce
import com.pharbers.aqll.util.msdConfig._
import com.pharbers.aqll.util.fileConfig._

case class dbdumpCmd(val db : String, val coll : String) extends shellCmdExce {
    val cmd = "/usr/bin/mongodump " +
        DBConfig(db, coll, s"${dumpdb}", Some(s"${dumpdb_ip}"), Some(2017), Some("Pharbers"), Some("Pharbers2017.")).toArgs
}
