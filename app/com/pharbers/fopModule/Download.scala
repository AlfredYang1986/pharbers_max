package com.pharbers.fopModule

import java.io.{File, FileInputStream}

import com.pharbers.common.algorithm.max_path_obj

/**
  * Created by jeorch on 18-6-5.
  */
object Download {
    def downloadFile(name : String) : Array[Byte] = {
        val filepath = max_path_obj.p_exportPath + name
        val file = new File(filepath)
        val reVal : Array[Byte] = new Array[Byte](file.length.intValue)
        new FileInputStream(file).read(reVal)
        reVal
    }
}
