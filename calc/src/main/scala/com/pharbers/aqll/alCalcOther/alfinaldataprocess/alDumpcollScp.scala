package com.pharbers.aqll.alCalcOther.alfinaldataprocess

import com.pharbers.aqll.common.alCmd.dbcmd.dbdumpCmd
import com.pharbers.aqll.common.alCmd.scpcmd.scpCmd
import com.pharbers.aqll.common.alFileHandler.databaseConfig._
import com.pharbers.aqll.common.alFileHandler.fileConfig._
import com.pharbers.aqll.common.alFileHandler.serverConfig._

/**
	* Created by liwei on 17-3-17.
	*/
case class alDumpcollScp() {
	def apply(sub_uuid: String, serverHost: String) = {
//		dbdumpCmd(db1, sub_uuid, dumpdb, dbuser, dbpwd, dbhost, dbport.toInt).excute
//		scpCmd(s"${dumpdb}${db1}/${sub_uuid}.bson.gz",s"${scpPath}", serverHost, serverUser).excute
		dbdumpCmd(db1, sub_uuid, "/Users/qianpeng/Desktop/" + dumpdb, dbuser, dbpwd, dbhost, dbport.toInt).excute
	}
}