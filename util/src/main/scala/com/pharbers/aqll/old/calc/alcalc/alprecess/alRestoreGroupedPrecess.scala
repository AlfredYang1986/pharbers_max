package com.pharbers.aqll.old.calc.alcalc.alprecess

import com.pharbers.aqll.old.calc.alcalc.alFileHandler.altext.FileOpt

class alRestoreGroupedPrecess extends alRestorePrecess {
    override def pathProxy(path : String) : String = {
        val tf = FileOpt(path)
        if (tf.isDir) tf.lstFiles.head
        else path
    }
}