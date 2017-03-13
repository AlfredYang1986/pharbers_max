package com.pharbers.aqll.alcalc.almain

import akka.actor.{Actor, ActorLogging, ActorRef, FSM, Props}
import akka.routing.BroadcastPool
import com.pharbers.aqll.alcalc.aljobs.alJob.grouping_jobs
import com.pharbers.aqll.alcalc.aljobs.aljobstates.alMaxCalcJobStates._
import com.pharbers.aqll.alcalc.aljobs.aljobstates.{alMasterJobIdle, alPointState}
import com.pharbers.aqll.alcalc.aljobs.aljobtrigger.alJobTrigger.{calc_avg_job, concert_calc_result, _}
import com.pharbers.aqll.alcalc.almaxdefines.alMaxProperty
import com.pharbers.aqll.alcalc.alstages.alStage
import com.pharbers.aqll.alcalc.alprecess.alprecessdefines.alPrecessDefines._
import com.pharbers.aqll.alcalc.aljobs.alJob._

import scala.concurrent.stm.atomic
import scala.concurrent.stm.Ref
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object alCalcActor {
    def props : Props = Props[alCalcActor]
}

class alCalcActor extends Actor 
                     with ActorLogging 
                     with FSM[alPointState, String] 
                     with alCreateConcretCalcRouter {

    startWith(alMasterJobIdle, "")

    when(alMasterJobIdle) {
        case Event(can_sign_job(), _) => {
            sender() ! sign_job_can_accept()
            stay()
        }

        case Event(calc_job(p), _) => {
            atomic { implicit tnx =>
                concert_ref() = Some(p)
                println(s"calc finally $p")
            }

            val cj = worker_calc_core_split_jobs(Map(worker_calc_core_split_jobs.max_uuid -> p.uuid, worker_calc_core_split_jobs.calc_uuid -> p.subs.head.uuid))
            context.system.scheduler.scheduleOnce(0 seconds, self, calcing_job(cj))
            goto(calc_coreing) using ""
        }
    }

    when(calc_coreing) {
        case Event(calcing_job(cj), _) => {
            val result = cj.result
            val (p, sb) = result.get.asInstanceOf[(String, List[String])]

            val q = sb.map (x => alMaxProperty(p, x, Nil))
            atomic { implicit tnx =>
                result_ref() = Some(alMaxProperty(concert_ref.single.get.get.uuid, p, q))
                adjust_index() = -1
            }
            concert_router ! concert_adjust()

            goto(calc_maxing) using ""
        }
    }

    when(calc_maxing) {
        case Event(concert_calc_sum_result(sub_uuid, sum), _) => {
            val r = result_ref.single.get.map (x => x).getOrElse(throw new Exception("must have runtime property"))

            r.subs.find (x => x.uuid == sub_uuid).map { x =>
                x.isSumed = true
                x.sum = sum
                r.sum = r.sum ++: sum
            }.getOrElse(Unit)

            println(r.sum)

            if (r.subs.filterNot (x => x.isSumed).isEmpty) {
                r.sum = (r.sum.groupBy(_._1) map { x =>
                    (x._1, (x._2.map(z => z._2._1).sum, x._2.map(z => z._2._2).sum, x._2.map(z => z._2._3).sum))
                }).toList
                r.isSumed = true

                val st = context.actorSelection("akka://calc/user/splitreception")
                st ! calc_sum_result(r.parent, r.uuid, r.sum)
            }
            stay()
        }
        case Event(calc_avg_job(uuid, avg), _) => {
            val r = result_ref.single.get.map (x => x).getOrElse(throw new Exception("must have runtime property"))

            if (r.parent == uuid)
                concert_router ! concert_calc_avg(r, avg)

            stay()
        }
        case Event(concert_calc_result(sub_uuid, v, u), _) => {
            println(sub_uuid, v, u)
            val r = result_ref.single.get.map (x => x).getOrElse(throw new Exception("must have runtime property"))

            r.subs.find (x => x.uuid == sub_uuid).map { x =>
                x.isCalc = true
                x.finalValue = v
                x.finalUnit = u
            }.getOrElse(Unit)

            if (r.subs.filterNot (x => x.isCalc).isEmpty) {
                r.subs.map(_.finalValue).foreach (println)
                r.subs.map(_.finalUnit).foreach (println)
                println(sub_uuid)

                r.finalValue = r.subs.map(_.finalValue).sum
                r.finalUnit = r.subs.map(_.finalUnit).sum
                r.isCalc = true

                val st = context.actorSelection("akka://calc/user/splitreception")
                st ! calc_final_result(r.parent, r.uuid, r.finalValue, r.finalUnit)
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

        case Event(calc_avg_job(_, _), _) => {
            // do nothing
            stay()
        }

        case Event(concert_adjust_result(_), _) => {
            atomic { implicit tnx =>
                adjust_index() = adjust_index() + 1
                sender() ! concert_adjust_result(adjust_index())
            }

            if (adjust_index.single.get == 3) {
                concert_router ! concert_calc(result_ref.single.get.get)
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
        context.actorOf(BroadcastPool(4).props(alConcertCalcActor.props), name = "concert-calc-router")
}