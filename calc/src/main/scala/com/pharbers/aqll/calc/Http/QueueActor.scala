package com.pharbers.aqll.calc.Http

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props, Terminated}
import akka.cluster.Cluster
import com.pharbers.aqll.calc.maxmessages.excelJobStart
import com.pharbers.aqll.calc.util.{GetProperties, ListQueue}

import scala.concurrent.stm.atomic


/**
  * Created by Faiz on 2017/1/19.
  */
object QueueActor {
    def props = Props[QueueActor]
}

case class ThreadQueue()
class QueueActor extends Actor with ActorLogging {
	val queue: Receive = {
		case ThreadQueue() => {
			atomic { implicit thx =>
				//println(s"listmq2 ==========${ListQueue.msgtmp.get}")
				if(ListQueue.msgtmp.get.size != 0){
					val node = ListQueue.listnode.find(x => x._1 == 0)
					node match {
						case None => Unit
						case Some(n) => {
							println(n._2)
							val address = n._2.path.address.toString
							val serverIP = address.substring((address lastIndexOf("@"))+1,address lastIndexOf(":"))
							println(s"serverIP = $serverIP")
							val local = GetProperties loadConf("File.conf") getString ("SCP.Upload_Client_File_Path")
							println(s"local = $local")
							val from = GetProperties loadConf("File.conf") getString("SCP.Upload_Calc_File_Path").toString
							println(s"from = $from")
							val filename = "123456.doc"//(ListQueue.msgtmp.get).apply(0).asInstanceOf[excelJobStart].filename
							val map = Map("local" -> (local+filename), "from" -> from)
							val atc = context.actorOf(ScpCopyActor.props)
							context.watch(atc)
							atc ! SCPServerInfo(serverIP, map, n._2, (ListQueue.msgtmp.get).apply(0))
							//n._2 ! (ListQueue.msgtmp.get).apply(0)
							ListQueue.ListNode_Queue((1, n._2))
							ListQueue.ListMq_Queue_del
						}
					}
				}
			}
		}
		case Terminated(a) => {
			println("scp cpoy is ok")
			context.unwatch(a)
			context.stop(a)
		}
		case _ => ???
	}

	def receive = queue
}
