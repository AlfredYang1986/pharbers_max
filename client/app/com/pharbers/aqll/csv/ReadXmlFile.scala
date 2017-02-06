package com.pharbers.aqll.csv

/**
  * Created by Wli on 2017/2/6 0006.
  */
object ReadXmlFile {

    val someXml : String = "xml/FileExport.xml"

    def getFieldContent(fn : String , str : String) : List[String] = {
        var header : List[String] = ((xml.XML.loadFile(someXml) \ "header" \ str).map (x => x.text)).toList
        var tail : List[String] = ((xml.XML.loadFile(someXml) \ "tail" \ str).map (x => x.text)).toList
        var province = ((xml.XML.loadFile(someXml) \ "body" \ "province" \ str).map (x => x.text)).toList
        var city = ((xml.XML.loadFile(someXml) \ "body" \ "city" \ str).map (x => x.text)).toList
        var hospital = ((xml.XML.loadFile(someXml) \ "body" \ "hospital" \ str).map (x => x.text)).toList
        var body : List[String] = fn match {
            case "省份数据" => province
            case "城市数据" => province ++ city
            case "医院数据" => province ++ city ++ hospital
        }
        header ++ body ++ tail
    }
}
