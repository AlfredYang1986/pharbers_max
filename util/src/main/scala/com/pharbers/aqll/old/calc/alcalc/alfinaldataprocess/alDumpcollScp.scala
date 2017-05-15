package com.pharbers.aqll.old.calc.alcalc.alfinaldataprocess

import com.pharbers.aqll.old.calc.alcalc.alcmd.dbcmd.dbdumpCmd
import com.pharbers.aqll.old.calc.alcalc.alcmd.scpcmd.scpCmd
import com.pharbers.aqll.old.calc.util.GetProperties

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
	dbdumpCmd("Max_Cores", uuid).excute
	scpCmd(s"${GetProperties.dumpdb}Max_Cores/${uuid}.bson.gz",s"${GetProperties.scpPath}","aliyun215","root").excute
}

//class alDumpcollScp(uuid: String) {
//	scpCmd(s"${GetProperties.dumpdb}Max_Cores/${uuid}.bson.gz",s"${GetProperties.scpPath}","aliyun215","root").excute
//}