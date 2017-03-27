package com.pharbers.aqll.calc.mail

import com.pharbers.aqll.calc.mail.MailAddress._

/**
  * Created by Wli on 2017/3/13 0013.
  */

object Mail {
    def apply(t: Throwable) = {
        new Mail(t)
    }
    def apply(context: String) = {
        new Mail(context)
    }
    def apply(t: Throwable, subject: String) = {
        new Mail(t, subject)
    }
    def apply(context: String, subject: String) = {
        new Mail(context, subject)
    }
}

class Mail() {
    //发送给哪些邮箱，多个邮箱之前用“，”分隔
    var to = mail_to
    //发件箱
    var from = mail_from
    //发件箱的密码
    var password = mail_password
    //简单邮件传送协议服务器
    var smtpHost = mail_smtpHost
    //邮件主题
    var subject = ""
    //邮件内容
    var privateContext = ""
    //抄送给哪些邮箱，多个邮箱之前用“，”分隔
    var cc = ""
    //密送给哪些邮箱，多个邮箱之前用“，”分隔
    var bcc = ""

    def this(context:String) {
        this
        this.privateContext = context
    }

    def this(t: Throwable) {
        this
        context_=(t)
    }

    def this(context:String,subject:String){
        this(context)
        this.subject = subject
    }

    def this(t: Throwable,subject:String){
        this(t)
        this.subject = subject
    }

    def context_=(t: Throwable) {
        this.privateContext = t + "\n" + t.getMessage
    }

    def context_=(context: String) {
        this.privateContext = context
    }

    def context:String = {
        this.privateContext
    }

    def setTo(to: String): this.type ={
        this.to = to
        this
    }

    def setFrom(from: String): this.type = {
        this.from = from
        this
    }

    def setPassword(password: String): this.type = {
        this.password = password
        this
    }

    def setSmptHost(smtpHost: String): this.type = {
        this.smtpHost = smtpHost
        this
    }

    def setSubject(subject: String): this.type = {
        this.subject = subject
        this
    }

    def setContext(context: String): this.type = {
        this.privateContext = context
        this
    }
    def setContext(t: Throwable): this.type = {
        context_=(t)
        this
    }
    def setCc(cc: String): this.type = {
        this.cc = cc
        this
    }

    def setBcc(bcc: String): this.type = {
        this.bcc = bcc
        this
    }
}
