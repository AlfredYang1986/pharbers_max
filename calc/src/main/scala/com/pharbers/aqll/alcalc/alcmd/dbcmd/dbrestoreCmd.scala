package com.pharbers.aqll.alcalc.alcmd.dbcmd

import com.pharbers.aqll.alcalc.alcmd.shellCmdExce
import com.pharbers.aqll.util.fileConfig._
import com.pharbers.aqll.util.msdConfig._

case class dbrestoreCmd(val db : String, val coll : String, val dir : String) extends shellCmdExce {
    val cmd = "/usr/bin/mongorestore " +
        DBInputConfig(db, coll, s"/root/${scpPath}$dir", Some(s"${restoredb_ip}"), Some(2017), Some("Pharbers"), Some("Pharbers2017.")).toArgs
}

case class dblocalrestoreCmd(val db : String, val coll : String, val dir : String) extends shellCmdExce {
    val cmd = "/usr/bin/mongorestore " +
        DBInputConfig(db, coll, s"/root/program/${dumpdb}Max_Cores/$dir", Some(s"${localrestoredb_ip}"), Some(2017), Some("Pharbers"), Some("Pharbers2017.")).toArgs
}