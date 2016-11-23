package com.pharbers.aqll.calc.excel.common

abstract class commonProductObjectTrait {
    def commonObjectCondition : String
    def getHospNum : java.lang.Long 
    def getUploadYear : java.lang.Integer
    def getUploadMonth : java.lang.Integer
    def getSumValue : java.lang.Double
    def getVolumeUnit : java.lang.Double
}