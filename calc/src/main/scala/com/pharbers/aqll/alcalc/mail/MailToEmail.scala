package com.pharbers.aqll.calc.mail

/**
  * Created by Wli on 2017/3/13 0013.
  */
import com.pharbers.aqll.calc.util.dao.from
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.BasicDBObject
import com.mongodb.DBObject

import scala.collection.immutable.List

object MailToEmail extends App{
    //两种方案：
    //  方案一：HTTP发送公司名，然后再计算完成后query公司表，查询对应邮箱地址，调用邮件接口，传参发送邮件（如果http传参不麻烦的话，还是方案二比较省事）。
    //  方案二：HTTP同事发送公司名、邮箱地址，等计算完成后，直接调用邮件接口，传参发送邮件。
    getEmail("098f6bcd4621d373cade4e832627b4f6")

    def getEmail(company: String): String = {
        val conditions : List[DBObject] = List(new BasicDBObject("Company_Id",company))
        val result = (from db () in company where conditions).selectAggregate(resultData(_)).toList.head
        println(result)
        result
    }

    def resultData(x: MongoDBObject) : String = {
        x.getAs[String]("e_mail").get
    }
}


