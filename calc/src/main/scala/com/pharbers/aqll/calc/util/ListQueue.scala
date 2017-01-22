package com.pharbers.aqll.calc.util


import akka.actor.{Actor, ActorRef, ActorSelection, Props}
import com.typesafe.config.Config

import scala.collection.mutable.ListBuffer

/**
  * Created by Faiz on 2017/1/18.
  */
object ListQueue {
    val listmq = ListBuffer[AnyRef]()
    val listnode = ListBuffer[(Int, ActorRef)]()
    def ListMq_Queue(anyref: AnyRef) = {
        listmq.append(anyref)
    }
    def ListNode_Queue(actorref: (Int, ActorRef)) = {
        if(listnode.indexWhere(x => x._2.equals(actorref._2)) < 0) {
            listnode.append(actorref)
        }else{
            val index = listnode.indexWhere(x => x._2.equals(actorref._2))
            listnode.update(index, (actorref._1, actorref._2))
        }
    }

    def ListMq_Queue_del = {
        listmq.remove(0)
    }
    def ListNode_Queue_del(actorref: ActorRef) = {
        val index = listnode.indexWhere(x => x._2.equals(actorref))
        listmq.remove(index)
    }
}

//case class ListQueueFree(str: String)
//
//object ListQueueActor {
//    def props = Props[ListQueueActor]
//}
//class ListQueueActor extends Actor {
//    def receive = {
//        case ListQueueFree(str) =>
//            println("in ListQueueFree")
//            ListQueue.ListNode_Queue(0, null, str)
//        case _ => ???
//    }
//}

