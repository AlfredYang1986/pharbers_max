package com.pharbers.aqll.calc.mail

/**
  * Created by Wli on 2017/3/13 0013.
  */
import com.pharbers.aqll.calc.util.dao.{_data_connection_basic, from}
import com.mongodb.BasicDBObject
import com.mongodb.DBObject

import scala.collection.immutable.List

object MailToEmail{
    //两种方案：
    //  方案一：HTTP发送公司名，然后再计算完成后query公司表，查询对应邮箱地址，调用邮件接口，传参发送邮件（如果http传参不麻烦的话，还是方案二比较省事）。
    //  方案二：HTTP同事发送公司名、邮箱地址，等计算完成后，直接调用邮件接口，传参发送邮件。

    def getEmail(company: String): String = {
        val conditions : List[DBObject] = List(new BasicDBObject("Company_Id",company))
        val email = (from db () in "Company" where conditions).select( x => x)(_data_connection_basic).head.getAs[String]("E-Mail").getOrElse("pqian@pharbers.com")
        println(email)
        email
    }
}


