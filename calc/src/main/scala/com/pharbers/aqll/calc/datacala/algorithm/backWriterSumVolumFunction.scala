package com.pharbers.aqll.calc.datacala.algorithm

import scala.Stream
import scala.collection.mutable.ArrayBuffer

import com.pharbers.aqll.calc.excel.model.integratedData
import com.pharbers.aqll.calc.excel.model.modelRunData

sealed class endException extends Exception
sealed class continueException extends Exception

object backWriterSumVolumFunction {
    val con = new continueException
    def apply(data_max : Stream[modelRunData], integratedData : Stream[integratedData])
              (stl : modelRunData => String)(str : integratedData => String) : Stream[modelRunData] = {
  
        var dt_max_left : Stream[modelRunData] = data_max
        var it_data_left : Stream[integratedData] = integratedData
        var dt_max_new : ArrayBuffer[modelRunData] = ArrayBuffer.empty
    
        def inputValue(dt_max : Stream[modelRunData], it_data : Stream[integratedData], dt_max_new : ArrayBuffer[modelRunData]) : Unit = { 
          var tmp = 0
          try {
              dt_max match {
              case head #:: smax => {
                   val condition = stl(head)
                   it_data match {
                     case iter #:: idt => {
                         val condition_idt = str(iter)
                         
                         if (condition < condition_idt) { 
                             dt_max_new += head
                             tmp = 1
                             inputValue(smax, it_data, dt_max_new) 
                         
                         } else if (condition > condition_idt) { 
                             tmp = 2
                             inputValue(dt_max, idt, dt_max_new)
                         } else {
                             if (head.uploadYear == iter.uploadYear 
                              && head.uploadMonth == iter.uploadMonth
                              && head.minimumUnitCh == iter.minimumUnitCh
                              && head.hospId == iter.hospNum){
                                 
                                 head.sumValue = iter.sumValue
                                 head.volumeUnit = iter.volumeUnit
                                 dt_max_new += head 
                                 tmp = 3
                                 inputValue(smax, it_data, dt_max_new)
                             } else {
                                 dt_max_new += head
                                 tmp = 1
                                 inputValue(smax, it_data, dt_max_new) 
                             }
                         }
                     }
                     case Stream.Empty => {
                       dt_max_new ++= dt_max
                       throw new endException
                     }
                   }
              }
              case Stream.Empty => throw new endException
            }
          } catch {
            case ex : java.lang.StackOverflowError => {
                tmp match {
                  case 0 => {
                     dt_max_left = dt_max
                     it_data_left = it_data
                  } case 1 => {
                     dt_max_left = dt_max.tail
                     it_data_left = it_data
                  } case 2 => {
                     dt_max_left = dt_max
                     it_data_left = it_data.tail
                  } case 3 => {
                     dt_max_left = dt_max.tail
                     it_data_left = it_data
                  }
                }
                 throw con
            }
          }
        }
   
        def inputValueAcc(dt_max : Stream[modelRunData], it_data : Stream[integratedData]) : Unit = {
            try {
                inputValue(dt_max, it_data, dt_max_new)
            } catch {
              case ex : continueException => {
                inputValueAcc(dt_max_left, it_data_left)
              }
              case ex : endException => Unit
              case ex : java.lang.StackOverflowError => {
                println("stack overflow error")
                Unit
              }
            }
        } 
  
        inputValueAcc(dt_max_left, it_data_left)
        dt_max_new.toStream
    }
}