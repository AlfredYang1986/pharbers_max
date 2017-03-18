package com.pharbers.aqll.alcalc.alfinaldataprocess

import com.pharbers.aqll.alcalc.alcmd.dbcmd.dbdumpCmd
import com.pharbers.aqll.alcalc.alcmd.scpcmd._
import com.pharbers.aqll.alcalc.alcmd.dbcmd._
import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.calc.util.dao.{_data_connection, from}
/**
  * Created by Wli on 17-3-17.
  */
object alDumpcollScp {
	def apply(sub_uuid : String): alDumpcollScp = new alDumpcollScp(sub_uuid)
}

class alDumpcollScp(sub_uuid : String) {
	dbdumpCmd("Max_Cores", sub_uuid).excute
//	cpCmd(s"/home/faiz/program/scpdb/Max_Cores/${sub_uuid}.bson.gz","/home/faiz/program/cp").excute
	scpCmd(s"/home/faiz/program/scpdb/Max_Cores/${sub_uuid}.bson.gz","/home/faiz/program/cp","59.110.31.215","root")
}

//		dbdumpCmd("Max_Cores", "0d93bb12-6c05-4ead-855a-05c5baacafda").excute
//		cpCmd("/home/faiz/program/scpdb/Max_Cores/0d93bb12-6c05-4ead-855a-05c5baacafda.bson.gz", "/home/faiz/program/cp/").excute
//		dbrestoreCmd("Max_Cores","company_temp","0d93bb12-6c05-4ead-855a-05c5baacafda").excute
//		_data_connection.getCollection("company_temp").createIndex(MongoDBObject("Index" -> 1))
