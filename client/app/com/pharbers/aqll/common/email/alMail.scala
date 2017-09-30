package com.pharbers.aqll.common.email

import org.apache.commons.mail.HtmlEmail

/**
  * Created by qianpeng on 2017/5/23.
  */

case class StmConf(from: String = "yangyuanpig@163.com",
                   pwd: String = "Abcde196125",
                   port: Int = 25,
                   host: String = "smtp.163.com",
                   tls: Boolean = false,
                   ssl: Boolean = false)

trait MailTrait {

	var subject: String = ""
	var context: String = ""

	def setSubject(sub: String): Mail

	def setContext(cont: String): Mail
	
	def sendTo(mail: String)(implicit stmc: StmConf): String
}

case class Mail() extends MailTrait {

    override def setSubject(sub: String): Mail = {
        this.subject = sub
        this
    }

	override def setContext(cont: String): Mail = {
		this.context = cont
		this
	}
	
	override def sendTo(mail: String)(implicit stmc: StmConf): String = {
		val email = new HtmlEmail
		email.setHostName(stmc.host)
		email.setSSLOnConnect(stmc.ssl)
		email.setAuthentication(stmc.from, stmc.pwd)
		email.setSmtpPort(stmc.port)
		email.setFrom(stmc.from)
        email.addTo(mail)
//	    email.setSubject(subject)
//		email.setMsg(context)

		email.setHtmlMsg(context)
		email.setCharset("UTF-8")
		
		email.send()
	}
}

