package com.pharbers.aqll.alCalcOther.mail

/**
  * Created by Wli on 2017/3/13 0013.
  */
import java.util.{Date, Properties}
import javax.mail._
import javax.mail.internet._

object MailAgent {
    def apply(mail:Mail) = {
        new MailAgent(mail)
    }
    def apply(t: Throwable) = {
        new MailAgent(Mail(t))
    }
    def apply(context: String) = {
        new MailAgent(Mail(context))
    }
    def apply(t: Throwable, subject: String) = {
        new MailAgent(Mail(t, subject))
    }
    def apply(context: String, subject: String) = {
        new MailAgent(Mail(context, subject))
    }
}

class MailAgent(mail: Mail) {
    //(to: String, from: String, password: String, subject: String, smtpHost: String)
    val props = new Properties()
    props.put("mail.smtp.auth", "true")
    val session = Session.getDefaultInstance(props)
    val message = new MimeMessage(session)

    def sendMessage() = {
        message.setFrom(new InternetAddress(mail.from))
        setToCcBccRecipients
        message.setSentDate(new Date())
        message.setSubject(mail.subject)
        message.setText(mail.context,"UTF-8")
        val transport = session.getTransport("smtp")
        //println(s"smtpHost=${mail.smtpHost} from=${mail.from} password=${mail.password}")
        transport.connect(mail.smtpHost, mail.from, mail.password)
        transport.sendMessage(message, message.getAllRecipients())
    }

    // throws AddressException, MessagingException
    private def setToCcBccRecipients {
        setMessageRecipients(mail.to, Message.RecipientType.TO)
        if (mail.cc != null && "".ne(mail.cc)) {
            setMessageRecipients(mail.cc, Message.RecipientType.CC)
        }
        if (mail.bcc != null && "".ne(mail.bcc)) {
            setMessageRecipients(mail.bcc, Message.RecipientType.BCC)
        }
    }

    // throws AddressException, MessagingException
    private def setMessageRecipients(recipient: String, recipientType: Message.RecipientType) {
        // had to do the asInstanceOf[...] call here to make scala happy
        val addressArray = buildInternetAddressArray(recipient).asInstanceOf[Array[Address]]
        if ((addressArray != null) && (addressArray.length > 0)) {
            message.setRecipients(recipientType, addressArray)
        }
    }

    // throws AddressException
    private def buildInternetAddressArray(address: String): Array[InternetAddress] = {
        // could test for a null or blank String but I'm letting parse just throw an exception
        return InternetAddress.parse(address)
    }
}
