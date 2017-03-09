package com.pharbers.aqll.calc.export

import com.pharbers.aqll.calc.util.GetProperties
import com.pharbers.aqll.calc.util.csv.scala.CSVWriter
import scala.collection.immutable.List
import scala.collection.mutable.ListBuffer

/**
  * Created by Wli on 2017/3/9.
  */
object WriteInCsv {
    /*Rread Csv Head*/
    def WriteCSVHead(writer : CSVWriter, datatype: String) {
        var str = datatype match {
            case "省份数据" => "Provice"
            case "城市数据" => "City"
            case "医院数据" => "Hospital"
        }
        writer.writeRow(((xml.XML.loadFile(GetProperties.loadConf("File.conf").getString("SCP.DownClient_Export_Title")) \ "body" \ str).map (x => x.text)).toList)
    }

    /*Wirte Csv Body*/
    def WriteCSVBody(result : List[Map[String,Any]], writer : CSVWriter, datatype: String) {
        result.foreach { x =>
            val lb : ListBuffer[AnyRef] = ListBuffer[AnyRef]()
            lb.append(x.get("Date").get.asInstanceOf[String])
            datatype match {
                case "省份数据" => {
                    lb.append(x.get("Provice").get.asInstanceOf[String])
                    lb.append(x.get("Market").get.asInstanceOf[String])
                }
                case "城市数据" => {
                    lb.append(x.get("Provice").get.asInstanceOf[String])
                    lb.append(x.get("City").get.asInstanceOf[String])
                    lb.append(x.get("Market").get.asInstanceOf[String])
                }
                case "医院数据" => {
                    lb.append(x.get("Provice").get.asInstanceOf[String])
                    lb.append(x.get("City").get.asInstanceOf[String])
                    lb.append(x.get("Panel_ID").get.asInstanceOf[String])
                    lb.append(x.get("Market").get.asInstanceOf[String])
                }
            }
            lb.append(x.get("Product").get.asInstanceOf[String])
            lb.append(f"${x.get("f_sales").get.toString.toDouble}%1.2f")
            lb.append(f"${x.get("f_units").get.toString.toDouble}%1.2f")
            writer.writeRow(lb.toList)
        }
    }
}
