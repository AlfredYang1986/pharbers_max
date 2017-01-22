package com.pharbers.aqll.calc.split

/**
  * Created by Faiz on 2017/1/21.
  */
import akka.actor.ActorRef
import akka.cluster.Member
case class Registration(mb: Member) extends Serializable
case class FreeListQueue(act: ActorRef) extends Serializable
trait EventMessage extends Serializable