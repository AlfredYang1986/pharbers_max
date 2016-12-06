package com.pharbers.aqll

import com.pharbers.aqll.excel.dispose._
import com.pharbers.aqll.excel.common.ReadFileData
import scala.math._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.Imports.DBObject
import com.pharbers.aqll.util.MD5
import com.pharbers.aqll.util.dao._
import com.pharbers.aqll.excel.model._
import scala.math._
import scala.io.Source
import java.io.File
import java.io.PrintWriter

object test extends App{

//    val excel_file_name = """file/excel/Hospital.xlsx"""
//    val excel_file_name1 = """file/excel/Products.xlsx"""
    
//    val hospdata : List[Hospital] = ReadFileData.hospdatadataobj(excel_file_name)
//    val productdata : List[Products] = ReadFileData.productdataobj(excel_file_name1)
    
    //.groupBy(x => x(0)).map( y => Map(y._1 -> y._2.map(z => z(1)).distinct)).toList
    //val lst = productdata.groupBy( x => x.getTrade_Name).map( y => Map("Prod_Id" -> MD5.md5(y._1), "Trade_Name" -> y._1, "Package_Quantity" -> y._2.map(z => z.getPackage_Quantity).distinct))
//    lst foreach { x=> println(x)}
    
//    def getbulk(coll_name : String) : BulkWriteOperation = {
//        _data_connection.getCollection(coll_name).initializeUnorderedBulkOperation
//    }
    
//    WriteInBasicData.insertHospitalBasicInfo(excel_file_name)
//    WriteInBasicData.insertProductBasicInfo(excel_file_name1)
//    WriteInCoresData.insertHospitalCoresInfo(excel_file_name)
//    WriteInCoresData.insertProductsCoresInfo(excel_file_name1)
    
    
//    val lst = productdata.groupBy(x => x.getTrade_Name).map(a => Map(
//            "MiniProdInfo_Id" -> MD5.md5(a._1), 
//            "Products" -> Map("lst" -> (Map("Ch" -> a._1,"En" -> "")),"Package_Quantity" -> (a._2.groupBy(y => y.getPackage_Quantity).map(b => b._1))), 
//            "Genericname" -> Map("lst" -> (Map("Ch" -> "", "En" -> "")).toArray), 
//            "DosageForm" -> Map("lst" -> (a._2.groupBy(z => z.getDosageform).map(b => Map("Ch" -> b._1, "En" -> "")))), 
//            "DrugSpecification" -> Map("lst" -> (a._2.groupBy(v => v.getDrugspecification).map(c => Map("Ch" -> "", "En" -> c._1)))), 
//            "Manufacturer" -> Map("lst" -> a._2.groupBy(w => w.getManufacturer_Name).map(d => Map("Ch" -> d._1, "En" -> "")))
//        ))
//    println(lst)
    
    
//    val lst = productdata.filter(x => x.getTrade_Name.equals("安内真"))
//    println(lst.foreach( x=> println(x.getTrade_Name+"  "+x.getDosageform+"  "+x.getDrugspecification+"  "+x.getPackage_Quantity+"  "+x.getManufacturer_Name)))
    
//    val lst = productdata.groupBy( x => (x.getTrade_Name,x.getDosageform,x.getDrugspecification,x.getPackage_Quantity,x.getManufacturer_Name))
//    println(lst.size)
//    val writer = new PrintWriter(new File("G:/Product.csv" ))
//    lst.foreach( x=>
//        writer.write(x._1._1+","+x._1._2+","+x._1._3+","+x._1._4+","+x._1._5+"\t\n")    
//    )
//    writer.close()
}