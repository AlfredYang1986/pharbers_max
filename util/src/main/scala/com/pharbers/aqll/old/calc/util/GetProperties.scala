package com.pharbers.aqll.old.calc.util

import com.typesafe.config.{Config, ConfigFactory}

/**
  * Created by Faiz on 2017/1/17.
  */
object GetProperties {

	import java.io.FileInputStream
	import java.util.Properties

	def loadProperties(filename: String): Properties = {
		val properties = new Properties()
		properties.load(new FileInputStream(Thread.currentThread().getContextClassLoader.getResource(filename).getPath))
		properties
	}

	def loadConf(filename: String): Config = {
		ConfigFactory.load(filename)
	}

	// TODO : Akka singleton地址
	def singletonPaht = loadConf("cluster-listener.conf").getString("cluster-listener.Node.main")

	def root = loadConf("File.conf").getString("SCP.root")

	def program = loadConf("File.conf").getString("SCP.program")

	// TODO : Python输出与Manage上传的HospitalData地址
	def fileBase = loadConf("File.conf").getString("File.FileBase_FilePath")

	def hospitalData = loadConf("File.conf").getString("File.Upload_HospitalData_File")

	def outPut = loadConf("File.conf").getString("File.OutPut_File")

	def python = loadConf("File.conf").getString("File.Python_File")

	def export_file = loadConf("File.conf").getString("File.Export_File")

	def export_xml = loadConf("File.conf").getString("File.Export_xml")

	def client_cpa_file = loadConf("File.conf").getString("File.Client_Cpa")

	def client_gycx_file = loadConf("File.conf").getString("File.Client_Gycx")

	def manage_file = loadConf("File.conf").getString("File.Manage_File")

	// TODO : Max计算输出地址
	def memorySplitFile = loadConf("File.conf").getString("SCP.Memory_Split_File")

	def sync = loadConf("File.conf").getString("SCP.sync")

	def group = loadConf("File.conf").getString("SCP.group")

	def calc =  loadConf("File.conf").getString("SCP.calc")

	def fileTarGz = loadConf("File.conf").getString("SCP.File_Tar_Gz")

	def scpPath = program + loadConf("File.conf").getString("SCP.scp_path")

	def dumpdb = loadConf("File.conf").getString("SCP.dumpdb")

	// TODO : 数据库备份还原IP
	def dumpdb_ip = loadConf("msd.conf").getString("DataBase.dbdump-ip")

	def restoredb_ip = loadConf("msd.conf").getString("DataBase.dbrestore-ip")

	def localrestoredb_ip = loadConf("msd.conf").getString("DataBase.dblocalrestore-ip")

	// TODO : Mail发送消息
	def mail_context = loadConf("mail.conf").getString("Mail.context")

	def mail_subject = loadConf("mail.conf").getString("Mail.subject")

	// TODO : EmChat的Org
	def org_name = loadConf("mail.conf").getString("EmChat.org_name")

	def app_name = loadConf("mail.conf").getString("EmChat.app_name")

	def grant_type = loadConf("mail.conf").getString("EmChat.grant_type")

	def client_id = loadConf("mail.conf").getString("EmChat.client_id")

	def client_secret = loadConf("mail.conf").getString("EmChat.client_secret")

	// TODO : 任务删除指定文件的时间 Hours = 3 小时 Minutes = 0 分钟 Seconds = 10 秒
	def hours = loadConf("File.conf").getInt("SCP.RemoveTime.Hours")

	def minutes = loadConf("File.conf").getInt("SCP.RemoveTime.Minutes")

	def seconds = loadConf("File.conf").getInt("SCP.RemoveTime.Seconds")

}
