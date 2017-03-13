package com.pharbers.aqll.alcalc.alstages

import java.util.UUID

import com.pharbers.aqll.alcalc.aldata.alStorage
import com.pharbers.aqll.alcalc.alprecess.alPrecess

/**
  * Created by Alfred on 10/03/2017.
  */

object alStage {
    def apply() : alStage = new alInitStage
    def apply(path : String) : alStage = {
        val tmp = new alPresisStage
        tmp.storages = path :: Nil
        tmp
    }
    def apply(files : List[String]) : alPresisStage = {
        val tmp = new alPresisStage
        tmp.storages = files
        tmp
    }

    def apply(data : List[alStorage]): alMemoryStage = {
        val tmp = new alMemoryStage
        tmp.storages = data
        tmp
    }
}

trait alStage {
    var storages : List[AnyRef] = Nil
    def isCalc = false

    def canLength : Boolean = false
    def length : Int = {
        println("only Memory can calc length")
        ???
    }
}

class alInitStage extends alStage
class alMemoryStage extends alStage {
    override def canLength : Boolean = true
    override def isCalc: Boolean = storages.find (x => !x.asInstanceOf[alStorage].isCalc) == None
    override def length: Int = storages.map (x => x.asInstanceOf[alStorage].length).sum
}
class alPresisStage extends alStage
