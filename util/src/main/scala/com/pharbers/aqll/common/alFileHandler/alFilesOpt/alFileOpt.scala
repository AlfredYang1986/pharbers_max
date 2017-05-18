package com.pharbers.aqll.common.alFileHandler.alFilesOpt

import java.io.{File, FileWriter, PrintWriter}

import scala.io.Source

/**
  * Created by qianpeng on 2017/5/12.
  */

object alFileOpt {
	def convertString(str: String): String =
		str.charAt(str.length - 1).toString match {
			case "\\" => str.substring(0, str.lastIndexOf("\\"))
			case "/" => str.substring(0, str.lastIndexOf("/"))
			case _: String => str
		}

	def apply(path: String): alFileOpt = new alFileOpt(convertString(path))(convertString(path))
}

class alFileOpt(path: String)(oldPath: String) {
	val f = new File(path)

	def exHideListFile: List[String] = f.listFiles.filter(x => x.isFile && !x.isHidden).map(_.getPath).toList

	def listAllFiles: List[String] = f.listFiles.map(_.getPath).toList

	def isExists = f.exists

	def createFile = f.createNewFile

	def createDir = f.mkdir

	def isDir : Boolean = f.isDirectory()

	def remove = f.delete

	def removeAllFiles: Boolean = {
		if(isDir) {
			listAllFiles foreach { x =>
				if(!alFileOpt(x).removeAllFiles) false
			}
		}
		f.delete
	}

	def removeCurFiles: Boolean = {
		if(isDir) {
			exHideListFile foreach { x =>
				if(!new alFileOpt(x)(oldPath).removeAllFiles) false
			}
		}
		if(f.getPath != oldPath) f.delete else false
	}

	def pushData2File(lst : List[Any]) = {
		val writer = new PrintWriter(f)
		lst foreach (x => writer.println(x))
		writer.close()
	}

	def appendData2File(lst : List[Any]): Unit = {
		val writer = new FileWriter(path, true)
		lst foreach (x => writer.write(x.toString + "\n"))
		writer.flush()
		writer.close()
	}

	def requestDataFromFile(f : String => Any) : List[Any] = Source.fromFile(path).getLines().map(f(_)).toList

	def enumDataWithFunc(f : String => Unit) = Source.fromFile(path).getLines().foreach(f(_))

}
