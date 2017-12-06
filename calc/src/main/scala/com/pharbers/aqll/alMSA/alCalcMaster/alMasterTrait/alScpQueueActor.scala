//package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait
//
//import akka.actor.{Actor, ActorLogging, ActorRef, Props}
//import com.pharbers.aqll.alMSA.alMaxCmdMessage.{alCmdActor, scpend, scpmsg}
//
//import scala.concurrent.duration._
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.stm._
//import alScpQueueActor._
//import com.pharbers.aqll.alMSA.alCalcMaster.alMaxMaster.{pushToScpQueue, scpSchedule}
//
///**
//  * Created by clock on 17-9-6.
//  */
//trait alScpQueueTrait { this: Actor =>
//    val scan_queue_schedule = context.system.scheduler.schedule(0 second,1 second,self,scpSchedule())
//
//    def scanQueue() = {
//        if(residue_run_number.single.get > 0){
//            scpQueue.single.get match {
//                case head :: tail => {
//                    atomic{implicit what =>
//                        scpQueue() = tail
//                        residue_run_number() = residue_run_number() - 1
//                    }
//                    val cmdActor = context.actorOf(alCmdActor.props())
//                    cmdActor.tell(scpmsg(head._1,head._2,head._3,head._4), head._5)
//                }
//
//                case Nil => Unit
//                case _ => throw new Exception("queue error")
//            }
//        }
//    }
//}
//
//object alScpQueueActor{
//    def props(s: ActorRef) : Props = Props(new alScpQueueActor(s))
//
//    val residue_run_number = Ref(4)
//    val scpQueue = Ref[List[(String,String,String,String,ActorRef)]](Nil)
//}
//
//class alScpQueueActor(s: ActorRef) extends Actor with ActorLogging{
//    override def receive: Receive = {
//        case pushToScpQueue(file,target,host,user) => push2queue(file,target,host,user)
//        case cmd: scpend =>
//            atomic{implicit what =>
//                alScpQueueActor.residue_run_number() = alScpQueueActor.residue_run_number() + 1
//            }
//            shutCameo()
//            s ! cmd
//        case _ => throw new Exception("queue error")
//    }
//
//
//    def push2queue(file: String, target: String, host: String, user: String) = {
//        scpQueue.single.get match {
//            case _:List[_] =>{
//                atomic { implicit what =>
//                    scpQueue() = scpQueue() :+ (file,target,host,user,self)
//                }
//            }
//            case _ => throw new Exception("queue error")
//        }
//    }
//
//    def shutCameo() = {
//        context.stop(self)
//    }
//}