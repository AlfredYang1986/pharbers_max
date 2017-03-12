package com.pharbers.aqll.alcalc.almain

import akka.actor.{Actor, ActorLogging, ActorRef, FSM, Props}
import akka.routing.BroadcastPool
import com.pharbers.aqll.alcalc.aljobs.alJob.grouping_jobs
import com.pharbers.aqll.alcalc.aljobs.aljobstates.alMaxCalcJobStates.{calc_coreing, calc_doing}
import com.pharbers.aqll.alcalc.aljobs.aljobstates.{alMasterJobIdle, alPointState}
import com.pharbers.aqll.alcalc.aljobs.aljobtrigger.alJobTrigger._
import com.pharbers.aqll.alcalc.almaxdefines.alMaxProperty
import com.pharbers.aqll.alcalc.alstages.alStage
import com.pharbers.aqll.alcalc.alprecess.alprecessdefines.alPrecessDefines._
import com.pharbers.aqll.alcalc.aljobs.alJob._

import scala.concurrent.stm.atomic
import scala.concurrent.stm.Ref
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * Created by BM on 11/03/2017.
  */
object alGroupActor {
    def props : Props = Props[alGroupActor]
}

class alGroupActor extends Actor
                     with ActorLogging
                     with FSM[alPointState, String]
                     with alCreateConcretCalcRouter {

    startWith(alMasterJobIdle, "")

    when(alMasterJobIdle) {
        case Event(can_sign_job(), _) => {
            sender() ! sign_job_can_accept()
            stay()
        }

        case Event(group_job(p), _) => {
            atomic { implicit tnx =>
                concert_ref() = Some(p)
            }

            println(Map(grouping_jobs.max_uuid -> p.uuid, grouping_jobs.group_uuid -> p.subs.head.uuid))
            val cj = grouping_jobs(Map(grouping_jobs.max_uuid -> p.uuid, grouping_jobs.group_uuid -> p.subs.head.uuid))
            context.system.scheduler.scheduleOnce(0 seconds, self, grouping_job(cj))
            goto(calc_coreing) using ""
        }
    }

    when(calc_coreing) {
        case Event(grouping_job(cj), _) => {
            println(s"开始根据CPU核数拆分线程")
            println(cj)

            val result = cj.result
            val (p, sb) = result.get.asInstanceOf[(String, List[String])]

            val q = sb.map (x => alMaxProperty(p, x, Nil))
            atomic { implicit tnx =>
                result_ref() = Some(alMaxProperty(concert_ref.single.get.get.uuid, p, q))
                adjust_index() = -1
            }
            println(result_ref.single.get)

            concert_router ! concert_adjust()

            goto(calc_doing) using ""
        }
    }

    when(calc_doing) {
        case Event(can_sign_job(), _) => {
            sender() ! service_is_busy()
            stay()
        }

        case Event(group_job(p), _) => {
            sender() ! service_is_busy()
            stay()
        }
        
        case Event(concert_group_result(sub_uuid), _) => {
            val r = result_ref.single.get.map (x => x).getOrElse(throw new Exception("must have runtime property"))
            
            r.subs.find (x => x.uuid == sub_uuid).map (x => x.grouped = true).getOrElse(Unit)
            
            if (r.subs.filterNot (x => x.grouped).isEmpty) {
                // group 4 to 1 and distinct
                val common = common_jobs()
                common.cur = Some(alStage(r.subs map (x => s"config/group/${x.uuid}")))
                common.process = restore_grouped_data() :: do_distinct() :: 
                                    do_calc() :: do_union() :: do_calc() :: 
                                    presist_data(Some(r.uuid), Some("group")) :: Nil
                common.result
                
                println(s"post group result ${r.parent} && ${r.uuid}")
            
                val st = context.actorSelection("akka://calc/user/splitreception")
                println(st)
                st ! group_result(r.parent, r.uuid)
                goto(alMasterJobIdle) using ""
            } else stay()
        }
    }

    whenUnhandled {
        case Event(can_sign_job(), _) => {
            sender() ! service_is_busy()
            stay()
        }

        case Event(group_job(p), _) => {
            sender() ! service_is_busy()
            stay()
        }

        case Event(concert_adjust_result(_), _) => {
            atomic { implicit tnx =>
                adjust_index() = adjust_index() + 1
                sender() ! concert_adjust_result(adjust_index())
            }

            if (adjust_index.single.get == 3) {
                concert_router ! concert_group(result_ref.single.get.get)
            }
            stay()
        }
    }

    val concert_ref : Ref[Option[alMaxProperty]] = Ref(None)            // 向上传递的，返回master的，相当于parent
    val result_ref : Ref[Option[alMaxProperty]] = Ref(None)             // 当前节点上计算的东西，相当于result
    val adjust_index = Ref(-1)
    val concert_router = CreateConcretCalcRouter
}

trait alCreateConcretCalcRouter { this : Actor =>
    def CreateConcretCalcRouter =
        context.actorOf(BroadcastPool(4).props(alConcertGroupActor.props), name = "concret-router")
}