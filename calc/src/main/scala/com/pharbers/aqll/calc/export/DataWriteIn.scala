package com.pharbers.aqll.calc.export

import java.io.File
import java.util.{Calendar, UUID}
import com.mongodb.DBObject
import com.mongodb.casbah.Imports.$and
import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.calc.export.WriteInCsv.{WriteCSVBody, WriteCSVHead}
import com.pharbers.aqll.calc.util.DateUtil.polishMonth
import com.pharbers.aqll.calc.util.GetProperties
import com.pharbers.aqll.calc.util.csv.scala.CSVWriter
import com.pharbers.aqll.calc.util.dao.from
import scala.collection.immutable.List
import com.pharbers.aqll.calc.export.GroupByData.{GroupByProvinceFunc,GroupByCityFunc,GroupByHospitalFunc}
/**
  * Created by Wli on 2017/3/9 0009.
  */
object DataWriteIn {
    /*Write Data To Csv*/
    def WriteDataToCSV(data : Map[String,Any], conditions : List[DBObject]) : String = {
        var datatype = data.get("datatype").get.asInstanceOf[String]
        var company = data.get("company").get.asInstanceOf[String]

        val path = GetProperties.loadConf("File.conf").getString("SCP.DownClient_Export_FilePath")
        val file : File = new File(path)
        if(!file.exists()) file.mkdir()
        val fileName = UUID.randomUUID + ".csv"
        val file1 : File = new File(path + fileName)
        val writer = CSVWriter.open(file1,"GBK")

        WriteCSVHead(writer, datatype)
        var first = 0
        var step = 10000
        var result: List[Map[String,Any]] = List.empty

        val sum = (from db() in company where $and(conditions)).count
        while (first < sum) {
            val resulttemp = (from db() in company where $and(conditions)).selectSkipTop(first)(step)("Date")(finalResult(_)).toList
            datatype match {
                case "省份数据" => {
                    result = GroupByProvinceFunc(resulttemp)(result)
                }
                case "城市数据" => {
                    result = GroupByCityFunc(resulttemp)(result)
                }
                case "医院数据" => {
                    result = GroupByHospitalFunc(resulttemp)(result)
                }
            }
            if(sum - first < step){step = sum - first}
            first += step
            println(s"$first / $sum")
        }
        WriteCSVBody(result,writer,datatype)
        fileName
    }

    def finalResult(x : MongoDBObject) : Map[String,Any] = {
        val timeDate = Calendar.getInstance
        timeDate.setTimeInMillis(x.getAs[Number]("Date").get.longValue)
        var yearmonth = s"${timeDate.get(Calendar.YEAR).toString}${polishMonth((timeDate.get(Calendar.MONTH)+1).toString)}"
        Map(
            "Panel_ID" -> x.getAs[String]("Panel_ID").get,
            "Date" -> yearmonth,
            "Provice" -> x.getAs[String]("Provice").get,
            "City" -> x.getAs[String]("City").get,
            "Market" -> x.getAs[String]("Market").get,
            "Product" -> x.getAs[String]("Product").get,
            "f_sales" -> s"${x.getAs[Number]("f_sales").get.doubleValue}",
            "f_units" -> s"${x.getAs[Number]("f_units").get.doubleValue}"
        )
    }
}
