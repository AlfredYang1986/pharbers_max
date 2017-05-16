package com.pharbers.aqll.alcalc.alfinaldataprocess

import com.pharbers.aqll.common.alCmd.dbcmd.dbdumpCmd
import com.pharbers.aqll.common.alCmd.scpcmd.scpCmd
import com.pharbers.aqll.alcalc.alCommon.fileConfig._
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
	dbdumpCmd("Max_Cores", uuid, dumpdb, "Pharbers", "Pharbers2017.").excute
	scpCmd(s"${dumpdb}Max_Cores/${uuid}.bson.gz",s"${scpPath}","aliyun215","root").excute
}

//class alDumpcollScp(uuid: String) {
//	scpCmd(s"${GetProperties.dumpdb}Max_Cores/${uuid}.bson.gz",s"${GetProperties.scpPath}","aliyun215","root").excute
//}