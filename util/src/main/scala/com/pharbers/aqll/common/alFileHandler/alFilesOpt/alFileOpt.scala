package com.pharbers.aqll.common.alFileHandler.alFilesOpt

import java.io.{File, FileWriter, PrintWriter, RandomAccessFile}

import com.pharbers.memory.pages.flushMemory

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
			listAllFiles foreach { x =>
				if(!new alFileOpt(x)(oldPath).removeCurFiles) false
			}
		}
		
		if(f.getPath != oldPath) f.delete else true
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

	def appendData2File2(lst : List[Any]): Unit = {
		val buff = flushMemory(path)
		lst foreach (x => buff.appendLine(x.toString))
		buff.flush
		buff.close
	}

	def requestDataFromFile(f : String => Any) : List[Any] = {
		val s = Source.fromFile(path)
		val lst = s.getLines().map(f(_)).toList
		s.close()
		lst
	}

	def enumDataWithFunc(f : String => Unit) = {
		val s = Source.fromFile(path)
		s.getLines().foreach(f(_))
		s.close()
	}

}
