package com.pharbers.aqll.db.shellcmd

case class DBConfig(val db : String,
                    val coll : String,
                    val out : String,
                    val host : Option[String] = None,
                    val port : Option[Int] = None,
                    val username : Option[String] = None,
                    val password : Option[String] = None
                   ) {

    def toArgs : String = {
        (if (host.isEmpty) ""
        else s"--host=${host.get} ") +
        (if (port.isEmpty) ""
        else s"--port=${port.get} ") +
        (if (username.isEmpty) ""
        else s"--username=${username.get} ") +
        (if (password.isEmpty) ""
        else s"--password=${password.get} ") +
        s"--db=$db --collection=$coll --gzip --out=$out"
    }
}

case class DBInputConfig(val db : String,
                    val coll : String,
                    val out : String,
                    val host : Option[String] = None,
                    val port : Option[Int] = None,
                    val username : Option[String] = None,
                    val password : Option[String] = None
                   ) {

    def toArgs : String = {
        (if (host.isEmpty) ""
        else s"--host=${host.get} ") +
            (if (port.isEmpty) ""
            else s"--port=${port.get} ") +
            (if (username.isEmpty) ""
            else s"--username=${username.get} ") +
            (if (password.isEmpty) ""
            else s"--password=${password.get} ") +
            s"--db=$db --collection=$coll --gzip --dir=$out/$coll.bson.gz"
    }
}
