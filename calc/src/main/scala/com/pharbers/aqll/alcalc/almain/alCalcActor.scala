package com.pharbers.aqll.alcalc.almain

import java.util.UUID

import akka.actor.{Actor, ActorLogging, FSM, Props}
import akka.routing.BroadcastPool
import com.pharbers.aqll.alcalc.alcmd.pkgcmd.unPkgCmd
import com.pharbers.aqll.alcalc.alfinaldataprocess.{alDumpcollScp, alLocalRestoreColl, alRestoreColl}
import com.pharbers.aqll.alcalc.aljobs.aljobstates.alMaxCalcJobStates._
import com.pharbers.aqll.alcalc.aljobs.aljobstates.{alMasterJobIdle, alPointState}
import com.pharbers.aqll.alcalc.aljobs.aljobtrigger.alJobTrigger.{calc_avg_job, concert_calc_result, _}
import com.pharbers.aqll.alcalc.almaxdefines.{alCalcParmary, alMaxProperty}
import com.pharbers.aqll.alcalc.alprecess.alprecessdefines.alPrecessDefines._
import com.pharbers.aqll.alcalc.aljobs.alJob._
import com.pharbers.aqll.alcalc.aljobs.alPkgJob
import com.pharbers.aqll.util.GetProperties

import scala.concurrent.stm.atomic
import scala.concurrent.stm.Ref
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object alCalcActor {
    def props : Props = Props[alCalcActor]

    val core_number = 4
}

class alCalcActor extends Actor 
                     with ActorLogging 
                     with FSM[alPointState, alCalcParmary]
                     with alCreateConcretCalcRouter
                     with alPkgJob{

    import alCalcActor.core_number

    startWith(alMasterJobIdle, new alCalcParmary(""))

    when(alMasterJobIdle) {
        case Event(can_sign_job(), _) => {
            sender() ! sign_job_can_accept()
            stay()
        }

        case Event(calc_job(p, parm), data) => {
            data.uuid = parm.uuid
            data.company = parm.company
            data.year = parm.year
            data.market = parm.market
            atomic { implicit tnx =>
                concert_ref() = Some(p)
                println(s"calc finally $p")
            }

            // TODO: 接收到Driver的信息后开始在各个机器上解压SCP过来的tar.gz文件，在开始Calc

            println(s"unCalcPkgSplit uuid = ${p.uuid}")

            cur = Some(new unPkgCmd(s"/root/program/scp/${p.uuid}", "/root/program/") :: Nil)
            process = do_pkg() :: Nil
            super.excute()

            calcjust_index.single.get match {
                case s: Int if s <= (p.subs.size - 1) =>
                    val mid = UUID.randomUUID.toString
                    val lst = (1 to core_number).map (x => worker_calc_core_split_jobs(Map(worker_calc_core_split_jobs.max_uuid -> p.uuid,
                        worker_calc_core_split_jobs.calc_uuid -> p.subs(s * core_number + x - 1).uuid,
                        worker_calc_core_split_jobs.mid_uuid -> mid))).toList
                    //val cj = worker_calc_core_split_jobs(Map(worker_calc_core_split_jobs.max_uuid -> p.uuid, worker_calc_core_split_jobs.calc_uuid -> p.subs(calcjust_index.single.get).uuid))
                    context.system.scheduler.scheduleOnce(0 seconds, self, calcing_job(lst, mid))
                    goto(calc_coreing) using data
                case _ =>
                    println("group no subs list")
                    stay()
            }

        }

        case Event(concert_calcjust_result(i), _) => {
            atomic { implicit tnx =>
                calcjust_index() = i
            }
            stay()
        }

        case Event(calc_need_files(uuid_file_path), _) => {
            println(s"fucking in my jobs = $uuid_file_path")
            stay()
        }
    }

    when(calc_coreing) {
        case Event(calcing_job(lst, p), data) => {

            val m = lst.map (_.result.get.asInstanceOf[(String, List[String])]._2).flatten.distinct

//            val result = cj.result
//            val (p, sb) = result.get.asInstanceOf[(String, List[String])]

            val q = m.map (x => alMaxProperty(p, x, Nil))
            atomic { implicit tnx =>
                result_ref() = Some(alMaxProperty(concert_ref.single.get.get.uuid, p, q))
                adjust_index() = -1
            }
            concert_router ! concert_adjust()

            goto(calc_maxing) using data
        }
    }

    when(calc_maxing) {
        case Event(concert_calc_sum_result(sub_uuid, sum), _) => {
            val r = result_ref.single.get.map (x => x).getOrElse(throw new Exception("must have runtime property"))

            r.subs.find (x => x.uuid == sub_uuid).map { x =>
                x.isSumed = true
                x.sum = sum
//                r.sum = r.sum ++: sum
            }.getOrElse(Unit)

            println(s"sub_uuid done $sub_uuid")

            if (r.subs.filterNot (x => x.isSumed).isEmpty) {
//                r.sum = (r.sum.groupBy(_._1) map { x =>
//                    (x._1, (x._2.map(z => z._2._1).sum, x._2.map(z => z._2._2).sum, x._2.map(z => z._2._3).sum))
//                }).toList
                r.isSumed = true
                val st = context.actorSelection(GetProperties.singletonPaht)

                r.subs.foreach { x =>


                    st ! calc_sum_result(r.parent, x.uuid, x.sum)
                }
            }
            stay()
        }
        case Event(calc_avg_job(uuid, avg), _) => {
            val r = result_ref.single.get.map (x => x).getOrElse(throw new Exception("must have runtime property"))

            println(s"avg")
            if (r.parent == uuid)
                concert_router ! concert_calc_avg(r, avg)

            stay()
        }
        case Event(concert_calc_result(sub_uuid, v, u), data) => {
            println(sub_uuid, v, u)
            val r = result_ref.single.get.map (x => x).getOrElse(throw new Exception("must have runtime property"))

            r.subs.find (x => x.uuid == sub_uuid).map { x =>
                x.isCalc = true
                x.finalValue = v
                x.finalUnit = u
            }.getOrElse(Unit)

            // TODO : 根据Sub_uuid备份数据库
            println(s"单个线程备份传输开始")
            alDumpcollScp(sub_uuid)
            println(s"单个线程备份传输结束")

            if (r.subs.filterNot (x => x.isCalc).isEmpty) {
                println(sub_uuid)
//                val uuid = UUID.randomUUID.toString
//                println(s"uuid = $uuid")
//                r.subs foreach { x =>
//                    println(s"还原开始")
//                    alLocalRestoreColl(uuid, x.uuid)
//                    println(s"还原结束")
//                }

//                println(s"集中备份开始")
//                alDumpcoll(uuid)
//                println(s"集中备份结束")





//                r.finalValue = r.subs.map(_.finalValue).sum
//                r.finalUnit = r.subs.map(_.finalUnit).sum
                r.isCalc = true

                val st = context.actorSelection(GetProperties.singletonPaht)
//                st ! calc_final_result(r.parent, r.uuid, r.finalValue, r.finalUnit)

                r.subs.foreach { x =>
                    st ! calc_final_result(r.parent, x.uuid, x.finalValue, x.finalUnit)
                }
                //st ! db_final_result(r.parent, uuid)


//                st ! calc_final_result(r.parent, r.uuid, r.finalValue, r.finalUnit)
                goto(alMasterJobIdle) using new alCalcParmary("")

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

        case Event(concert_adjust_result(_), data) => {
            atomic { implicit tnx =>
                adjust_index() = adjust_index() + 1
                sender() ! concert_adjust_result(adjust_index())
            }

            if (adjust_index.single.get == core_number - 1) {
                concert_router ! concert_calc(result_ref.single.get.get, data)
            }
            stay()
        }
    }

    val concert_ref : Ref[Option[alMaxProperty]] = Ref(None)            // 向上传递的，返回master的，相当于parent
    val result_ref : Ref[Option[alMaxProperty]] = Ref(None)             // 当前节点上计算的东西，相当于result
    val adjust_index = Ref(-1)
    val calcjust_index = Ref(-1)
    val concert_router = CreateConcretCalcRouter
}

trait alCreateConcretCalcRouter { this : Actor =>
    import alCalcActor.core_number
    def CreateConcretCalcRouter =
        context.actorOf(BroadcastPool(core_number).props(alConcertCalcActor.props), name = "concert-calc-router")
}