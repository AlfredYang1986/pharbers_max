package com.pharbers.aqll.old.calc.alcalc.alcmd.pyshell

import com.pharbers.aqll.old.calc.util.GetProperties._

case class PythonConfig(val company : String,val filename : String,yms : String) {
    def toArgs : String = {
      val path = fileBase + company + python +filename+ " " + company
      if(!yms.equals("")){
        path+" "+yms
      }else{
        path
      }
    }
}