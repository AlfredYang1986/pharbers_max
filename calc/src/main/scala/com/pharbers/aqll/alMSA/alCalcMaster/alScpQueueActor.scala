package com.pharbers.aqll.alMSA.alCalcMaster

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import com.pharbers.aqll.alMSA.alCalcMaster.alScpQueueActor.{ExcuteScanScpQueue, residue_run_number, scpQueue}
import com.pharbers.aqll.alMSA.alMaxCmdMessage.{alCmdActor, scpend, scpmsg}

import scala.concurrent.stm.{Ref, atomic}

/**
  * Created by clock on 17-9-6.
  */
object alScpQueueActor{
    def props(s: ActorRef) : Props = Props(new alScpQueueActor(s))

    case class PushToScpQueue(file: String, target: String, host: String, user: String)
    case class ExcuteScanScpQueue()

    val scpQueue = Ref[List[(String,String,String,String,ActorRef)]](Nil)
    val residue_run_number = Ref(5)
}


class alScpQueueActor(s: ActorRef) extends Actor with ActorLogging{
    import alScpQueueActor._

    override def receive: Receive = {
        case PushToScpQueue(file,target,host,user) => push2queue(file,target,host,user)
        case cmd: scpend =>
            atomic{implicit what =>
                alScpQueueActor.residue_run_number() = alScpQueueActor.residue_run_number() + 1
            }
            shutCameo()
            s ! cmd
        case _ => throw new Exception("queue error")
    }


    def push2queue(file: String, target: String, host: String, user: String) = {
        scpQueue.single.get match {
            case _:List[_] =>{
                atomic { implicit what =>
                    scpQueue() = scpQueue() :+ (file,target,host,user,self)
                }
            }
            case _ => throw new Exception("queue error")
        }
    }

    def shutCameo() = {
        println(s"stopping scp cameo END self === $self")
        context.stop(self)
    }
}

trait alMaxQueueTrait { this: Actor =>
    import scala.concurrent.duration._
    import scala.concurrent.ExecutionContext.Implicits.global
    val scan_queue_schedule = context.system.scheduler.schedule(0 second,1 second,self,ExcuteScanScpQueue())

    def scanQueue() = {
        if(residue_run_number.single.get > 0){
            scpQueue.single.get match {
                case head :: tail => {
                    atomic{implicit what =>
                        scpQueue() = tail
                        residue_run_number() = residue_run_number() - 1
                    }
                    val cmdActor = context.actorOf(alCmdActor.props())
                    cmdActor.tell(scpmsg(head._1,head._2,head._3,head._4), head._5)
                }

                case Nil => Unit
                case _ => throw new Exception("queue error")
            }
        }
    }
}