package com.pharbers.aqll.alcalc.alcmd.pyshell

import com.pharbers.aqll.util.fileConfig._

case class PythonConfig(company: String, filename: String, yms : String) {
    def toArgs : String = {
      val path = fileBase + company + python +filename+ " " + company
      if(!yms.equals("")){
        path+" "+yms
      }else{
        path
      }
    }
}