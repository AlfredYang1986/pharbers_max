package com.pharbers.aqll.calc.split

import akka.actor.{ActorPath, ActorSystem, RootActorPath, Terminated}
import akka.cluster.ClusterEvent._
import akka.cluster.{Member, MemberStatus}
import com.pharbers.aqll.calc.util.ListQueue

/**
  * Created by Faiz on 2017/1/21.
  */
class EventCollector extends ClusterEventListener{

    override def receive = {
        case MemberUp(member) =>
            println("====Member is Up: {}", member.address)
            register(member, getCollectorPath)
        case MemberExited(member) =>
            println("。。。。=Member is Exited: {}",member.address)
        case UnreachableMember(member) =>
            println("----Member detected as Unreachable: {}", member)
        case MemberRemoved(member, previousStatus) =>
            println("++++Member is Removed: {} after {}", member.address, previousStatus)
        case _: MemberEvent => // ignore

        case Registration(member) => {
            if(member.hasRole("splitworker")) {
                ListQueue.ListNode_Queue((0, sender))
                members = members :+ member
                println("Interceptor registered: " + sender)
                println("address: " + ListQueue.listnode)
            }
        }
        case FreeListQueue(act, old) => {
            val path = old.path.toString.substring(0, old.path.toString.lastIndexOf("user")-1)
            val m = members.find(x => x.address.toString.equals(path)).get
            //register(m, getCollectorPath)
            println(s"01 === ${ListQueue.listnode}")
            ListQueue.ListNode_Queue((0, act))
            println(s"02 === ${ListQueue.listnode}")
            ListQueue.ListNode_Queue_del(old)
            println(s"03 === ${ListQueue.listnode}")
        }
        case Terminated(a) =>
//            masters = masters.filterNot(_ == a)
//            ListQueue.ListNode_Queue((0, a))

            println(s"Terminated = $a")
            println(s"EventCollector In")
    }

    def getCollectorPath(member: Member): ActorPath = {
        println(RootActorPath(member.address))
        RootActorPath(member.address) / "user" / "splitreception"
    }

}
