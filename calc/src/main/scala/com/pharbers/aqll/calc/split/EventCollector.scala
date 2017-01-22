package com.pharbers.aqll.calc.split

import akka.actor.{ActorPath, RootActorPath, Terminated}
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
                println("adress2:"+sender.path.toString.substring(0, sender.path.toString.lastIndexOf("user")))
            }
        }
        case FreeListQueue(act) => {
            ListQueue.ListNode_Queue_del(act)
            val path = act.path.toString.substring(0, act.path.toString.lastIndexOf("user"))
            val m = members.find(x => x.address.toString.equals(path)).get
            register(m, getCollectorPath)
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
