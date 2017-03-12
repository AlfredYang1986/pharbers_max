package com.pharbers.aqll.alcalc.alfilehandler.altext

import java.io.{File, PrintWriter}

import scala.io.Source

/**
  * Created by Alfred on 09/03/2017.
  */
object FileOpt {
    def apply(path : String) : FileOpt = new FileOpt(path)
}

class FileOpt(val file : String) {
    val tf = new File(file)
    def isExist : Boolean = tf.exists()

    def pushData2File(lst : List[Any]) = {
        val writer = new PrintWriter(tf)
        lst foreach (x => writer.println(x))
        writer.close()
    }

    def requestDataFromFile(f : String => Any) : List[Any] = Source.fromFile(file).getLines().map(f(_)).toList

    def isDir : Boolean = tf.isDirectory()
    def createDir = tf.mkdir()
    def lstFiles : List[String] = tf.listFiles.filter(x => x.isFile && !x.isHidden).map(x => x.getPath).toList
}
