package com.pharbers.aqll.alcalc.alcmd.pyshell

case class PythonConfig(val company : String,val files : List[(String,String)],val year : String) {

    def toArgs : String = {
      var filessb = ""
      if(!files.isEmpty) files.foreach{
        x => filessb = filessb + " "+ x._1
      }
      (if(company.isEmpty) " " else " /Users/liwei/workspace/calc/src/main/scala/com/pharbers/aqll/alcalc/alcmd/pyshell/python/" +company+".py") +
      filessb + (if(year.isEmpty) " " else " "+year)
    }
}