package com.pharbers.aqll.old.calc.alcalc.mail

/**
  * Created by Wli on 2017/3/13 0013.
  */

import java.io.IOException
import com.pharbers.aqll.old.calc.alcalc.mail.MailAgent
import com.pharbers.aqll.old.calc.alcalc.mail.Mail
import com.pharbers.aqll.old.calc.util.GetProperties

object TestMail extends App{
    try {
        MailAgent(Mail(GetProperties.mail_context, GetProperties.mail_subject, "pqian@pharbers.com")).sendMessage()
        println("send is ok")
        //throw new IOException()
    } catch {
        case t: Throwable =>
            //val mail = new Mail().setTo("pqian@pharbers.com,wli@pharbers.com").setCc("wli@pharbers.com").setSubject("授信规则出异常了(测试，请忽略)").setContext(e)
            MailAgent(t,"授信规则有异常").sendMessage()
    }
}
