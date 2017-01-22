package com.pharbers.aqll.calc.split

import akka.actor.{Actor, ActorLogging, ActorPath, ActorRef, Address, RootActorPath}
import akka.cluster.{Cluster, Member, MemberStatus}
import akka.cluster.ClusterEvent._
import com.pharbers.aqll.calc.util.ListQueue

/**
  * Created by Faiz on 2017/1/17.
  */
abstract class ClusterEventListener extends Actor with ActorLogging {
    Cluster(context.system).subscribe(self, classOf[MemberEvent])
    val cluster = Cluster(context.system)
    var members = Seq[Member]()

    override def preStart(): Unit = {
        cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
              classOf[MemberUp], classOf[UnreachableMember], classOf[MemberEvent])
    }
    override def postStop(): Unit = {
        cluster.unsubscribe(self)
        println("stopstopstopstopstopstopstopstopstopstopstopstopstopstopstopstopstopstopstopstopstopstopstopstopstopstopstopstopstop")
    }

    def register(member: Member, createPath: (Member) => ActorPath): Unit = {
        val actorPath = createPath(member)
        println("Actor path: " + actorPath)
        val actorSelection = context.actorSelection(actorPath)
        actorSelection ! Registration(member)
    }



    override def receive ={
        case MemberJoined(member) =>
            println("Member Join")
        case MemberUp(member) =>
//            if(member.hasRole("splitworker")){
//                val port = member.address.port.get + 1000
//                val path = "akka.tcp://calc@127.0.0.1:"+port+"/user/sample"
//                ListQueue.ListNode_Queue((0, context.actorSelection(path), member.address.toString))
//            }
            println("Member Up")
        case MemberExited(member) =>
            println("Member Exited")
        case MemberRemoved(member, previousStatus) =>
            println("Member Removed")
        case x: MemberEvent =>
            println(s"MemberEvent: ${x}")

    }
}
