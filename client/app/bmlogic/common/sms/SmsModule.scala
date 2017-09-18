package bmlogic.common.sms

import play.api._
import play.api.libs.concurrent._

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits._
import akka.actor.Actor
import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout
import akka.actor.ActorRef
import java.io.UnsupportedEncodingException
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.FileInputStream
import org.apache.commons.httpclient.Header
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.NameValuePair
import org.apache.commons.httpclient.methods.PostMethod

case class sms(phoneNo: String, code: String)
case class invited(phoneNo: String, who: String)

class smsActor extends Actor {
	def receive = {
	  case sms(phoneNo, code) => 
	    val client = new HttpClient()
	  	val post = new PostMethod("http://gbk.sms.webchinese.cn")
	    post.addRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=gbk")
	    val br = new BufferedReader(new InputStreamReader(new FileInputStream("resource/sms_content"), "utf8"))
	    val data = "Uid=" + smsModule.userName + "&Key=" + smsModule.secertKey + "&smsMob=" + phoneNo + "&smsText=" + br.readLine.replace("11111", code)
	    post.setRequestBody(data)
	    client.executeMethod(post)
//	    val result = new String(post.getResponseBodyAsString().getBytes("gbk"))
//	    println(result)
	    post.releaseConnection();
	 
	  case invited(phoneNo, who) => 
		val client = new HttpClient()
	  	val post = new PostMethod("http://gbk.sms.webchinese.cn")
	    post.addRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=gbk")
	    val br = new BufferedReader(new InputStreamReader(new FileInputStream("resource/invitation_content"), "utf8"))
	    val data = "Uid=" + smsModule.userName + "&Key=" + smsModule.secertKey + "&smsMob=" + phoneNo + "&smsText=" + br.readLine.replace("11111", who)
//	    val phoneNo2 = "13720200856"
//	    val data = "Uid=" + smsModule.userName + "&Key=" + smsModule.secertKey + "&smsMob=" + phoneNo2 + "&smsText=" + br.readLine
	    post.setRequestBody(data)
	    client.executeMethod(post)
//	    val result = new String(post.getResponseBodyAsString().getBytes("gbk"))
//	    println(result)
	    post.releaseConnection();
		    
	  case _ => Unit
	}  
}

case class smsModule(smsActor : ActorRef) {
	implicit val timeout = Timeout(1 second)
	
	def sendSMS(phoneNo: String, code: String) = smsActor ! sms(phoneNo, code)
	def sendSMSs(msgs: List[sms]) = msgs foreach (x => smsActor ! x)

	def sendInvitation(phoneNo: String, who: String) = smsActor ! invited(phoneNo, who)
}

object smsModule {
	val userName = "abcdealfred"
	val secertKey = "fa8285b55d86d66e3f45"
  
	var smsCenter : Option[ActorRef] = None //Akka.system.actorOf(Props[ChatNotifycationCenter])
	def apply()(implicit app: Application) : smsModule = smsCenter.map(nc => new smsModule(nc)).getOrElse {
	  		smsCenter = Some(Akka.system(app).actorOf(Props[smsActor]))
	  		new smsModule(smsCenter.get)
	  	}
}