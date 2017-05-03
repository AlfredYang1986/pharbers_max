package com.pharbers.aqll.alcalc.alFileHandler.altext

import java.io.{File, FileWriter, PrintWriter}

import scala.io.Source

/**
  * Created by Alfred on 09/03/2017.
  */
object FileOpt {
    def convertString(str: String): String =
        str.charAt(str.length - 1).toString match {
            case "\\" => str.substring(0, str.lastIndexOf("\\"))
            case "/" => str.substring(0, str.lastIndexOf("/"))
            case _: String => str
        }
    def apply(path : String) : FileOpt = new FileOpt(convertString(path))(convertString(path))
}

class FileOpt(val file : String)(oldPath: String) {
    val tf = new File(file)
    def isExist : Boolean = tf.exists()

    def pushData2File(lst : List[Any]) = {
        val writer = new PrintWriter(tf)
        lst foreach (x => writer.println(x))
        writer.close()
    }

    def appendData2File(lst : List[Any]): Unit = {
        val writer = new FileWriter(file, true);
        lst foreach (x => writer.write(x.toString + "\n"))
        writer.flush()
        writer.close()
    }

    def requestDataFromFile(f : String => Any) : List[Any] = Source.fromFile(file).getLines().map(f(_)).toList

    def enumDataWithFunc(f : String => Unit) = Source.fromFile(file).getLines().foreach(f(_))

    def isDir : Boolean = tf.isDirectory()
    def createDir = tf.mkdir()
    def createFile = tf.createNewFile
    def lstFiles : List[String] = tf.listFiles.filter(x => x.isFile && !x.isHidden).map(x => x.getPath).toList
    def lstFiles2 : List[String] = tf.listFiles.map(x => x.getPath).toList

    def rmcAllFiles: Boolean = {
        if(tf.isDirectory) {
            lstFiles2 foreach { x =>
                val success = new FileOpt(x)(oldPath).rmcAllFiles
                if(!success) false
            }
        }
        if(tf.getPath != oldPath) tf.delete else false
    }

    def rmaAllFiles: Boolean = {
        if(tf.isDirectory) {
            lstFiles2 foreach { x =>
                val success = FileOpt(x).rmaAllFiles
                if(!success) false
            }
        }
        tf.delete
    }
}
