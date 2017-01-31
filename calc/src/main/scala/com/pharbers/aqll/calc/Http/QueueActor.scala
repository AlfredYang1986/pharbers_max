package com.pharbers.aqll.calc.Http

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props, Terminated}
import akka.cluster.Cluster
import com.pharbers.aqll.calc.util.ListQueue
import scala.concurrent.stm.atomic


/**
  * Created by Faiz on 2017/1/19.
  */
object QueueActor {
    def props = Props[QueueActor]
}

case class ThreadQueue()
    class QueueActor extends Actor with ActorLogging{
        val queue: Receive = {
            case ThreadQueue() => {
                atomic { implicit thx =>
                    println(s"listmq2 ==========${ListQueue.msgtmp.get}")
                    if(ListQueue.msgtmp.get.size != 0){
                        val node = ListQueue.listnode.find(x => x._1 == 0)
                        node match {
                            case None => Unit
                            case Some(n) => {
                                println(n._2)
                                println(n._2.path)
                                //n._2 ! (ListQueue.msgtmp.get).apply(0)
                                ListQueue.ListNode_Queue((1, n._2))
                                ListQueue.ListMq_Queue_del
                            }
                        }
                    }
                }

            }
            case _ => ???
        }

        def receive = queue
    }
