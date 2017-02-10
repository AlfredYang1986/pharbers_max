package com.pharbers.aqll.calc.util

import org.apache.tools.ant.Project
import org.apache.tools.ant.taskdefs.optional.ssh.Scp

/**
  * Created by faiz on 17-2-5.
  */
object ScpCopyFile {

	def apply(server: String, user: String, pass: String, map: Map[String, String], port: Int = 22) =
		copyfrom(server, port, user, pass, map)

	def copyfrom(server: String, port: Int, user: String, pass: String, map: Map[String, String]): Boolean = {
		val scp = new Scp
		scp.setServerAliveCountMax(100)
		scp.setPort(port)
		scp.setLocalFile(map.get("local").get)
		scp.setTodir(user + ":" + pass + "@" + server + ":" + map.get("from").get)
		scp.setProject(new Project)
		scp.setTrust(true)
		scp.execute
		println(scp.getFailonerror)
		scp.getFailonerror
	}
}
