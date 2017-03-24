package com.pharbers.aqll.alcalc.alcmd.pyshell

case class PythonConfig(val company : String) {

    def toArgs : String = {
      "/Users/liwei/FileBase/"+company+"/Python/"+company+".py "+company
    }
}