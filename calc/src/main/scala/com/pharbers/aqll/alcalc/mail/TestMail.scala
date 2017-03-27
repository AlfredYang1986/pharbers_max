package com.pharbers.aqll.calc.mail

/**
  * Created by Wli on 2017/3/13 0013.
  */

import java.io.IOException
import com.pharbers.aqll.calc.mail.MailAgent

object TestMail extends App{
    try {
        MailAgent(Mail("您好，您本次上传的数据已全部计算完成，请您及时核对确认，有问题可以回复邮件或者电话联系我们。","法伯科技运维支持")).sendMessage()
        //throw new IOException()
    } catch {
        case t: Throwable =>
            //val mail = new Mail().setTo("pqian@pharbers.com,wli@pharbers.com").setCc("wli@pharbers.com").setSubject("授信规则出异常了(测试，请忽略)").setContext(e)
            MailAgent(t,"授信规则有异常").sendMessage()
    }
}
