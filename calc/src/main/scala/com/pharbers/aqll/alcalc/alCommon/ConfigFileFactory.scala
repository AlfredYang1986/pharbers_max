package com.pharbers.aqll.alcalc.alCommon

import com.typesafe.config.ConfigFactory

/**
  * Created by clock on 2017/5/15.
  */
object msdConfig {
	val msd: IConfigFactory = ConfigFileFactory.getDBConfigFactory

	val dbhost = msd.getProperties("database.dbhost")
	val dbport = msd.getProperties("database.dbport")
	val dbuser = msd.getProperties("database.dbuser")
	val dbpwd = msd.getProperties("database.dbpwd")
	val db1 = msd.getProperties("database.db1")
	val db2 = msd.getProperties("database.db2")
	val dumpdb_ip = msd.getProperties("database.dbdump_ip")
	val restoredb_ip = msd.getProperties("database.dbrestore_ip")
	val localrestoredb_ip = msd.getProperties("database.dblocalrestore_ip")
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
	// TODO : 任务删除指定文件的时间 Hours = 3 小时 Minutes = 0 分钟 Seconds = 10 秒
	val hours = file.getProperties("SCP.RemoveTime.Hours").toInt
	val minutes = file.getProperties("SCP.RemoveTime.Minutes").toInt
	val seconds = file.getProperties("SCP.RemoveTime.Seconds").toInt
}


object mailConfig {
	val mail: IConfigFactory = ConfigFileFactory.getMailConfigFactory

	// TODO : Mail发送消息
	val mail_context = mail.getProperties("Mail.context")
	val mail_subject = mail.getProperties("Mail.subject")
	// TODO : EmChat的Org
	val org_name = mail.getProperties("EmChat.org_name")
	val app_name = mail.getProperties("EmChat.app_name")
	val grant_type = mail.getProperties("EmChat.grant_type")
	val client_id = mail.getProperties("EmChat.client_id")
	val client_secret = mail.getProperties("EmChat.client_secret")
}



object clusterListenerConfig {
	val clusterListener: IConfigFactory = ConfigFileFactory.getClusterListenerConfigFactory

	// TODO : Akka singleton地址
	val singletonPaht = clusterListener.getProperties("cluster-listener.Node.main")
}

/**
  * The singleton abstract factory is used to generate the specified configuration file factory.
  */
object ConfigFileFactory {
	val configFileMap = Map("database" -> new MsdConfigFactory("database.conf"),"mail" -> new MailConfigFactory("mail.conf"),"file" -> new FileConfigFactory("File.conf"),"cluster" -> new ClusterListenerConfigFactory("cluster-listener.conf"))

	def getDBConfigFactory = {
		configFileMap("database")
	}

	def getFileConfigFactory = {
		configFileMap("file")
	}

	def getMailConfigFactory = {
		configFileMap("mail")
	}

	def getClusterListenerConfigFactory = {
		configFileMap("cluster")
	}
}

/**
  * Configure factory trait
  */
trait IConfigFactory {
	def getProperties(configKey: String):String
}

class MsdConfigFactory(configFileName: String) extends IConfigFactory {
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

class FileConfigFactory(configFileName: String) extends IConfigFactory {
	val config = ConfigFactory.load(configFileName)

	override def getProperties(configKey: String):String = {
		if(configKey.equals("SCP.RemoveTime.Hours")||configKey.equals("SCP.RemoveTime.Minutesy")||configKey.equals("SCP.RemoveTime.Seconds")){
			config.getInt(configKey).toString
		}else{
			config.getString(configKey)
		}
	}
}

class ClusterListenerConfigFactory(configFileName: String) extends IConfigFactory {
	val config = ConfigFactory.load(configFileName)

	override def getProperties(configKey: String):String = {
		config.getString(configKey)
	}
}
