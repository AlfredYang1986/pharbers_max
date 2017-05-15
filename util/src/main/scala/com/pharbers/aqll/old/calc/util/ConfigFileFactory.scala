package com.pharbers.aqll.old.calc.util

import com.typesafe.config.ConfigFactory

/**
  * Created by clock on 2017/5/15.
  */
object msdConfig {
	val msd: IConfigFactory = ConfigFileFactory.getMsdConfigFactory

	def restoredb_ip = msd.getProperties("DataBase.dbrestore-ip")
	// TODO : 数据库备份还原IP
	def dumpdb_ip = msd.getProperties("DataBase.dbdump-ip")
	def localrestoredb_ip = msd.getProperties("DataBase.dblocalrestore-ip")
}

object fileConfig {
	val file: IConfigFactory = ConfigFileFactory.getFileConfigFactory

	def root = file.getProperties("SCP.root")
	def program = file.getProperties("SCP.program")
	// TODO : Python输出与Manage上传的HospitalData地址
	def fileBase = file.getProperties("File.FileBase_FilePath")
	def hospitalData = file.getProperties("File.Upload_HospitalData_File")
	def outPut = file.getProperties("File.OutPut_File")
	def python = file.getProperties("File.Python_File")
	def export_file = file.getProperties("File.Export_File")
	def export_xml = file.getProperties("File.Export_xml")
	def client_cpa_file = file.getProperties("File.Client_Cpa")
	def client_gycx_file = file.getProperties("File.Client_Gycx")
	def manage_file = file.getProperties("File.Manage_File")
	// TODO : Max计算输出地址
	def memorySplitFile = file.getProperties("SCP.Memory_Split_File")
	def sync = file.getProperties("SCP.sync")
	def group = file.getProperties("SCP.group")
	def calc =  file.getProperties("SCP.calc")
	def fileTarGz = file.getProperties("SCP.File_Tar_Gz")
	def scpPath = program + file.getProperties("SCP.scp_path")
	def dumpdb = file.getProperties("SCP.dumpdb")
	// TODO : 任务删除指定文件的时间 Hours = 3 小时 Minutes = 0 分钟 Seconds = 10 秒
	def hours = file.getProperties("SCP.RemoveTime.Hours")
	def minutes = file.getProperties("SCP.RemoveTime.Minutes")
	def seconds = file.getProperties("SCP.RemoveTime.Seconds")
}


object mailConfig {
	val mail: IConfigFactory = ConfigFileFactory.getMailConfigFactory

	// TODO : Mail发送消息
	def mail_context = mail.getProperties("Mail.context")
	def mail_subject = mail.getProperties("Mail.subject")
	// TODO : EmChat的Org
	def org_name = mail.getProperties("EmChat.org_name")
	def app_name = mail.getProperties("EmChat.app_name")
	def grant_type = mail.getProperties("EmChat.grant_type")
	def client_id = mail.getProperties("EmChat.client_id")
	def client_secret = mail.getProperties("EmChat.client_secret")
}



object clusterListenerConfig {
	val clusterListener: IConfigFactory = ConfigFileFactory.getClusterListenerConfigFactory

	// TODO : Akka singleton地址
	def singletonPaht = clusterListener.getProperties("cluster-listener.Node.main")
}

/**
  * The singleton abstract factory is used to generate the specified configuration file factory.
  */
object ConfigFileFactory {
	val configFileMap = Map("msd" -> new MsdConfigFactory("msd.conf"),"mail" -> new MailConfigFactory("mail.conf"),"file" -> new FileConfigFactory("File.conf"),"cluster" -> new ClusterListenerConfigFactory("cluster-listener.conf"))

	def getMsdConfigFactory = {
		configFileMap("msd")
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
