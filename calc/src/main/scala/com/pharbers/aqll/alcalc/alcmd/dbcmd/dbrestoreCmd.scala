package com.pharbers.aqll.alcalc.alcmd.dbcmd

import com.pharbers.aqll.alcalc.alcmd.shellCmdExce
import com.pharbers.aqll.calc.util.GetProperties

case class dbrestoreCmd(val db : String, val coll : String, val dir : String) extends shellCmdExce {
    val cmd = "/usr/bin/mongorestore " +
        DBInputConfig(db, coll, s"/root/${GetProperties.scpPath}$dir", Some(s"${GetProperties.restoredb_ip}"), Some(2017), Some("Pharbers"), Some("Pharbers2017.")).toArgs
}

case class dblocalrestoreCmd(val db : String, val coll : String, val dir : String) extends shellCmdExce {
    val cmd = "/usr/bin/mongorestore " +
        DBInputConfig(db, coll, s"/root/program/${GetProperties.dumpdb}Max_Cores/$dir", Some(s"${GetProperties.localrestoredb_ip}"), Some(2017), Some("Pharbers"), Some("Pharbers2017.")).toArgs
}