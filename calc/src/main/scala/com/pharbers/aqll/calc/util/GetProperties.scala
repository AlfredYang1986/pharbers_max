package com.pharbers.aqll.calc.util

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

	def singletonPaht = loadConf("cluster-listener.conf").getString("cluster-listener.node.main")

	def memorySplitFile = loadConf("File.conf").getString("SCP.Memory_Split_File")

	def sync = loadConf("File.conf").getString("SCP.sync")

	def group = loadConf("File.conf").getString("SCP.group")

	def calc =  loadConf("File.conf").getString("SCP.calc")

	def fileTarGz = loadConf("File.conf").getString("SCP.File_Tar_Gz")

}
