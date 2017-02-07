package com.pharbers.aqll.calc.util


import akka.actor.{Actor, ActorRef, ActorSelection, Props}
import com.typesafe.config.Config

import scala.collection.mutable.ListBuffer

import scala.concurrent.stm.Ref
import scala.concurrent.stm.atomic

/**
  * Created by Faiz on 2017/1/18.
  */
object ListQueue {
    val msgtmp = Ref(ListBuffer[AnyRef]())
    val listmq = ListBuffer[AnyRef]()
    val listnode = ListBuffer[(Int, ActorRef)]()

    def ListMq_Queue(anyref: AnyRef) = {
        atomic { implicit  thx =>
            //msgtmp() = msgtmp() ++: ListBuffer(anyref)
            msgtmp().append(anyref)
//            println(s"msgtmp.get == ==== == ${msgtmp.get}")
        }
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
        atomic { implicit  thx =>
            msgtmp().remove(0)
        }
        listmq.remove(0)
    }
    def ListNode_Queue_del(actorref: ActorRef) = {
        val index = listnode.indexWhere(x => x._2.equals(actorref))
        listnode.remove(index)
    }
}