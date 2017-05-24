package com.pharbers.aqll.alCalcOther.alMail

import com.pharbers.aqll.alCalaHelp.DBList
import org.apache.commons.mail._
import com.pharbers.aqll.common.alFileHandler.mailConfig._


/**
  * Created by qianpeng on 2017/5/23.
  */

case class StmConf(from: String = "project@pharbers.com",
                   pwd: String = "project",
                   port: Int = 25,
                   host: String = "smtp.ym.163.com",
                   tls: Boolean = false,
                   ssl: Boolean = false)

trait MailTrait extends DBList{

	var subject: String = mail_subject
	var context: String = mail_context
	
	def setSubject(sub: String): Mail
	
	def setContext(cont: String): Mail
	
	def sendTo(mail: String)(implicit stmc: StmConf): String
	
}

class Mail() extends MailTrait {
	
	override def setSubject(sub: String): Mail = {
		this.subject = sub
		this
	}
	
	override def setContext(cont: String): Mail = {
		this.context = cont
		this
	}
	
	override def sendTo(mail: String)(implicit stmc: StmConf): String = {
		val email = new SimpleEmail
		email.setHostName(stmc.host)
		email.setSSLOnConnect(stmc.ssl)
		email.setAuthentication(stmc.from, stmc.pwd)
		email.setSmtpPort(stmc.port)
		email.setFrom(stmc.from)
		email.addTo(mail)
		email.setSubject(subject)
		email.setMsg(context)
		email.send()
	}
}
