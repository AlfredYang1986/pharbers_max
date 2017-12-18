package com.pharbers.aqll.alCalcMemory.alprecess

import com.pharbers.alCalcMemory.alOther.alException.alException
import com.pharbers.alCalcMemory.alOther.alFileHandler.alFileHandlers
import com.pharbers.aqll.alCalaHelp.DefaultData
import com.pharbers.aqll.alCalc.almodel.java.IntegratedData
import com.pharbers.alCalcMemory.aldata.alStorage
import com.pharbers.alCalcMemory.alprecess.alPrecess
import com.pharbers.alCalcMemory.alstages.{alInitStage, alMemoryStage, alPresisStage, alStage}
import com.pharbers.aqll.common.alErrorCode.alErrorCode.errorToJson
import com.pharbers.aqll.common.alFileHandler.alExcelOpt.scala.alExcelDataParser

/**
  * Created by Alfred on 10/03/2017.
  * 　Modify by clock on 05/06/2017.
  */

case class excelHandlers() extends alExcelDataParser(new IntegratedData, DefaultData.integratedxmlpath_en, DefaultData.integratedxmlpath_ch) with alFileHandlers

class alReadExcelPrecess extends alPrecess {
    def precess(j : alStage) : List[alStage] = {
        
        def precessAcc(path : String) : alStorage = alStorage(path, excelHandlers())
        
        try {
            j match {
                case it : alInitStage => alStage(precessAcc(it.storages.head.toString) :: Nil) :: Nil
                case it : alPresisStage => alStage(it.storages.map(x => precessAcc(x.toString))) :: Nil
                case _ : alMemoryStage => alException(errorToJson("memory stage cannot precess"));Nil
            }
        } catch {
            case ex : OutOfMemoryError => alException(errorToJson("not enough memory")); throw ex
            case ex : Exception => alException(errorToJson("unknow error")); throw ex
        }
    }
}


