package com.pharbers.aqll.common.alCmd.pycmd

case class PyConfig(pyDir: String,
                    pyFileName: String,
                    args: Option[String] = None) {
    def toArgs: String = {
        ((if(pyDir.isEmpty) "" else pyDir) +
        (if(pyFileName.isEmpty) "" else pyFileName) + " " +
        (if(args.isEmpty) "" else args.get))
    }
}