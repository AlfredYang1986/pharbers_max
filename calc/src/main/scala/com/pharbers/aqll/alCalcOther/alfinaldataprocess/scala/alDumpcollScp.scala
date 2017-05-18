package com.pharbers.aqll.alCalcOther.alfinaldataprocess.scala

import com.pharbers.aqll.common.alFileHandler.databaseConfig._
import com.pharbers.aqll.common.alFileHandler.fileConfig._
import com.pharbers.aqll.common.alFileHandler.serverConfig._
import com.pharbers.aqll.common.alCmd.dbcmd.dbdumpCmd
import com.pharbers.aqll.common.alCmd.scpcmd.scpCmd


/**
  * Created by Wli on 17-3-17.
  */
object alDumpcollScp {
	def apply(sub_uuid : String): alDumpcollScp = new alDumpcollScp(sub_uuid)
}

//object alDumpcollScp {
//	def apply(uuid: String): alDumpcollScp = new alDumpcollScp(uuid)
//}

class alDumpcollScp(uuid : String) {
	dbdumpCmd(db1, uuid, dumpdb, dbuser, dbpwd, dbhost, dbport.toInt).excute
	scpCmd(s"${dumpdb}${db1}/${uuid}.bson.gz",s"${scpPath}", serverHost215, serverUser).excute
}

//class alDumpcollScp(uuid: String) {
//	scpCmd(s"${GetProperties.dumpdb}Max_Cores/${uuid}.bson.gz",s"${GetProperties.scpPath}","aliyun215","root").excute
//}