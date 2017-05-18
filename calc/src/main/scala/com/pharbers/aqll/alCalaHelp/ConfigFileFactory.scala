package com.pharbers.aqll.alCalaHelp

import com.typesafe.config.ConfigFactory

/**
  * Created by clock on 2017/5/15.
  */
object clusterListenerConfig {
	val clusterListener: IConfigFactory = ConfigFileFactory.getClusterListenerConfigFactory

	// TODO : Akka singleton地址
	val singletonPaht = clusterListener.getProperties("cluster-listener.Node.main")
}
object databaseConfig {
	val db: IConfigFactory = ConfigFileFactory.getDBConfigFactory

	val dbhost = db.getProperties("database.dbhost")
	val dbport = db.getProperties("database.dbport").toInt
	val dbuser = db.getProperties("database.dbuser")
	val dbpwd = db.getProperties("database.dbpwd")
	val db1 = db.getProperties("database.db1")
	val db2 = db.getProperties("database.db2")
	val dumpdb_ip = db.getProperties("database.dbdump_ip")
	val restoredb_ip = db.getProperties("database.dbrestore_ip")
	val localrestoredb_ip = db.getProperties("database.dblocalrestore_ip")
}
object emChatConfig {
	val emChat: IConfigFactory = ConfigFileFactory.getEmChatConfigFactory

	val orgName = emChat.getProperties("EmChat.org_name")
	val appName = emChat.getProperties("EmChat.app_name")
	val grantType = emChat.getProperties("EmChat.grant_type")
	val clientId = emChat.getProperties("EmChat.client_id")
	val clientSecret = emChat.getProperties("EmChat.client_secret")
}
object fileConfig {
	val file: IConfigFactory = ConfigFileFactory.getFileConfigFactory

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
	val mail: IConfigFactory = ConfigFileFactory.getMailConfigFactory

	// TODO : Mail发送消息
	val mail_context = mail.getProperties("Mail.context")
	val mail_subject = mail.getProperties("Mail.subject")
}
object serverConfig {
	val server: IConfigFactory = ConfigFileFactory.getServerConfigFactory

	// TODO : Server的用户
	val serverUser = server.getProperties("Server.user")
	val serverPass = server.getProperties("Server.pass")
	// TODO : ServerHost
	val serverHost215 = server.getProperties("Server.Host.aliyun215")
	val serverHost106 = server.getProperties("Server.Host.aliyun106")
	val serverHost50 = server.getProperties("Server.Host.aliyun50")
}
object timingConfig {
	val timing: IConfigFactory = ConfigFileFactory.getTimingConfigFactory

	// TODO : 任务删除指定文件的时间 Hours = 3 小时 Minutes = 0 分钟 Seconds = 10 秒
	val hours = timing.getProperties("Timing.Hours").toInt
	val minutes = timing.getProperties("Timing.Minutes").toInt
	val seconds = timing.getProperties("Timing.Seconds").toInt
}

/**
  * The singleton abstract factory is used to generate the specified configuration file factory.
  */
object ConfigFileFactory {
	val configFileMap = Map(
		"cluster" -> new ClusterListenerConfigFactory("cluster-listener.conf"),
		"db" -> new DBConfigFactory("database.conf"),
		"emChat" -> new EmChatConfigFactory("emChat.conf"),
		"file" -> new FileConfigFactory("file.conf"),
		"mail" -> new MailConfigFactory("mail.conf"),
		"server" -> new ServerConfigFactory("server.conf"),
		"timing" -> new TimingConfigFactory("timing.conf")
	)

	def getClusterListenerConfigFactory = {
		configFileMap("cluster")
	}
	def getDBConfigFactory = {
		configFileMap("db")
	}
	def getEmChatConfigFactory = {
		configFileMap("emChat")
	}
	def getFileConfigFactory = {
		configFileMap("file")
	}
	def getMailConfigFactory = {
		configFileMap("mail")
	}
	def getServerConfigFactory = {
	configFileMap("server")
	}
	def getTimingConfigFactory = {
	configFileMap("timing")
	}
}

/**
  * Configure factory trait
  */
trait IConfigFactory {
	def getProperties(configKey: String):String
}

class ClusterListenerConfigFactory(configFileName: String) extends IConfigFactory {
	val config = ConfigFactory.load(configFileName)

	override def getProperties(configKey: String):String = {
		config.getString(configKey)
	}
}
class DBConfigFactory(configFileName: String) extends IConfigFactory {
	val config = ConfigFactory.load(configFileName)

	override def getProperties(configKey: String):String = {
		if(configKey.equals("DB.dbport")){
			config.getInt(configKey).toString
		}else{
			config.getString(configKey)
		}
	}
}
class EmChatConfigFactory(configFileName: String) extends IConfigFactory {
	val config = ConfigFactory.load(configFileName)

	override def getProperties(configKey: String):String = {
		config.getString(configKey)
	}
}
class FileConfigFactory(configFileName: String) extends IConfigFactory {
	val config = ConfigFactory.load(configFileName)

	override def getProperties(configKey: String):String = {
		config.getString(configKey)
	}
}
class MailConfigFactory(configFileName: String) extends IConfigFactory {
	val config = ConfigFactory.load(configFileName)

	override def getProperties(configKey: String):String = {
		config.getString(configKey)
	}
}
class ServerConfigFactory(configFileName: String) extends IConfigFactory {
	val config = ConfigFactory.load(configFileName)

	override def getProperties(configKey: String):String = {
		config.getString(configKey)
	}
}
class TimingConfigFactory(configFileName: String) extends IConfigFactory {
	val config = ConfigFactory.load(configFileName)

	override def getProperties(configKey: String):String = {
		config.getInt(configKey).toString
	}
}


