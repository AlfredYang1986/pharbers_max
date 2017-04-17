package com.pharbers.aqll.alcalc.alcmd.pyshell

import com.pharbers.aqll.util.GetProperties.{fileBase, _}

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