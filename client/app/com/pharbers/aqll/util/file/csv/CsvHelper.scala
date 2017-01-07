package com.pharbers.aqll.util.file.csv

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
/**
  * Created by Wli on 2017/1/7.
  */
object CsvHelper {
    def writeFile(file : File, data : List[String]): Boolean = {
        try {
            var bw : BufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "GBK"))
            data.foreach { x =>
                bw.write(x)
                bw.newLine()
            }
            bw.close()
        } catch {
            case ex: FileNotFoundException =>{
                ex.printStackTrace()
            }
            case ex: IOException => {
                ex.printStackTrace()
            }
            false
        }
        true
    }
}
