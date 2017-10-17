package com.pharbers.aqll.common.alFileHandler

import com.typesafe.config.ConfigFactory

/**
  * Created by clock on 2017/5/17.
  */
object clusterListenerConfig {
	val clusterListener: IConfigFactory = CommonConfigFactory.getConfigFactory("cluster")

	// TODO : Akka singleton地址
	val singletonPaht = clusterListener.getProperties("cluster-listener.Node.main")
}
object databaseConfig {
	val db: IConfigFactory = CommonConfigFactory.getConfigFactory("database")

	val dbhost = db.getProperties("database.dbhost")
	val dbport = db.getProperties("database.dbport")
	val dbuser = db.getProperties("database.dbuser")
	val dbpwd = db.getProperties("database.dbpwd")
	val db1 = db.getProperties("database.db1")
	val db2 = db.getProperties("database.db2")
	val dumpdb_ip = db.getProperties("database.dbdump_ip")
	val restoredb_ip = db.getProperties("database.dbrestore_ip")
	val localrestoredb_ip = db.getProperties("database.dblocalrestore_ip")
}
object emChatConfig {
	val emChat: IConfigFactory = CommonConfigFactory.getConfigFactory("emChat")

	val orgName = emChat.getProperties("EmChat.org_name")
	val appName = emChat.getProperties("EmChat.app_name")
	val grantType = emChat.getProperties("EmChat.grant_type")
	val clientId = emChat.getProperties("EmChat.client_id")
	val clientSecret = emChat.getProperties("EmChat.client_secret")
}
object fileConfig {
	val file: IConfigFactory = CommonConfigFactory.getConfigFactory("file")

	val root = file.getProperties("SCP.root")
	val program = file.getProperties("SCP.program")
	// TODO : Python输出与Manage上传的HospitalData地址
	val fileBase = file.getProperties("File.FileBase_FilePath")
	val hospitalData = file.getProperties("File.Upload_HospitalData_File")
	val outPut = file.getProperties("File.OutPut_File")
	val python = file.getProperties("File.Python_File")
	val export_file = file.getProperties("File.Export_File")
	val export_xml = file.getProperties("File.Export_xml")
	val client_cpa_file = file.getProperties("File.Client_Cpa")
	val client_gycx_file = file.getProperties("File.Client_Gycx")
	val manage_file = file.getProperties("File.Manage_File")
	val Upload_Firststep_Filename = file.getProperties("File.Upload_Firststep_Filename")
	val Upload_Secondstep_Filename = file.getProperties("File.Upload_Secondstep_Filename")
	// TODO : Max计算输出地址
	val memorySplitFile = file.getProperties("SCP.Memory_Split_File")
	val sync = file.getProperties("SCP.sync")
	val group = file.getProperties("SCP.group")
	val calc =  file.getProperties("SCP.calc")
	val fileTarGz = file.getProperties("SCP.File_Tar_Gz")
	val scpPath = program + file.getProperties("SCP.scp_path")
	val dumpdb = file.getProperties("SCP.dumpdb")
}
object mailConfig {
	val mail: IConfigFactory = CommonConfigFactory.getConfigFactory("mail")

	// TODO : Mail发送消息
	val mail_context = mail.getProperties("Mail.context")
	val mail_subject = mail.getProperties("Mail.subject")
}
object serverConfig {
	val server: IConfigFactory = CommonConfigFactory.getConfigFactory("server")

	// TODO : Mail发送消息
	val serverUser = server.getProperties("Server.user")
	val serverPass = server.getProperties("Server.pass")
	// TODO : EmChat的Org
	val serverHost215 = server.getProperties("Server.Host.aliyun215")
	val serverHost106 = server.getProperties("Server.Host.aliyun106")
	val serverHost50 = server.getProperties("Server.Host.aliyun50")
}
object timingConfig {
	val timing: IConfigFactory = CommonConfigFactory.getConfigFactory("timing")

	// TODO : 任务删除指定文件的时间 Hours = 3 小时 Minutes = 0 分钟 Seconds = 10 秒
	val hours = timing.getProperties("Timing.Hours")
	val minutes = timing.getProperties("Timing.Minutes")
	val seconds = timing.getProperties("Timing.Seconds")
}
object akkaConfig {
	val akka: IConfigFactory = CommonConfigFactory.getConfigFactory("akka")
	val akkaIp = akka.getProperties("akka.http.ip")
	val akkaPort = akka.getProperties("akka.http.port")

}

object CommonConfigFactory {
	val configFactoryMap:Map[String,IConfigFactory] = Map(
		"cluster" ->  new ClusterListenerConfigFactory,
		"database" -> new DBConfigFactory,
		"emChat" -> new EmChatConfigFactory,
		"file" -> new FileConfigFactory,
		"mail" -> new MailConfigFactory,
		"server" -> new ServerConfigFactory,
		"timing" -> new TimingConfigFactory,
		"akka" -> new AkkaConfigFactory
	)

	def getConfigFactory(configFileName:String):IConfigFactory = {
		configFactoryMap(configFileName)
	}
}

/**
  * Configure factory trait
  */
trait IConfigFactory{
	val configFileName:String
	lazy val config = ConfigFactory.load(configFileName)

	def getProperties(configKey: String):String = {
		config.getString(configKey)
	}
}

class ClusterListenerConfigFactory() extends IConfigFactory{override val configFileName = "cluster-listener.conf"}
class DBConfigFactory  extends IConfigFactory{override val configFileName = "database.conf"}
class EmChatConfigFactory  extends IConfigFactory{override val configFileName = "emChat.conf"}
class FileConfigFactory  extends IConfigFactory{override val configFileName = "File.conf"}
class MailConfigFactory  extends IConfigFactory{override val configFileName = "mail.conf"}
class ServerConfigFactory  extends IConfigFactory{override val configFileName = "server.conf"}
class TimingConfigFactory  extends IConfigFactory{override val configFileName = "timing.conf"}
class AkkaConfigFactory  extends IConfigFactory{override val configFileName = "akka.conf"}
