package com.pharbers.aqll.alCalc.almain

import java.util.UUID

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorLogging, FSM, Props, Terminated}
import akka.routing.BroadcastPool
import com.pharbers.aqll.alCalaHelp.alMaxDefines.{alCalcParmary, alMaxProperty}
import com.pharbers.aqll.alCalcMemory.aljobs.alPkgJob
import com.pharbers.aqll.alCalcMemory.aljobs.aljobstates.{alMasterJobIdle, alPointState}
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger._
import com.pharbers.aqll.alCalcMemory.alprecess.alsplitstrategy.server_info
import com.pharbers.aqll.common.alCmd.pkgcmd.unPkgCmd
import com.pharbers.aqll.common.alDao.dataFactory._
import com.pharbers.aqll.common.alFileHandler.fileConfig._
import com.pharbers.aqll.common.alFileHandler.clusterListenerConfig._
import com.pharbers.aqll.alCalcEnergy.alCalcSupervisorStrategy
import com.pharbers.aqll.alCalcMemory.aljobs.alJob.worker_calc_core_split_jobs
import com.pharbers.aqll.alCalcMemory.aljobs.aljobstates.alMaxCalcJobStates.{calc_coreing, calc_maxing}
import com.pharbers.aqll.alCalcMemory.alprecess.alprecessdefines.alPrecessDefines.do_pkg
import com.pharbers.aqll.alCalcOther.alMessgae.alMessageProxy
import com.pharbers.aqll.alCalcOther.alfinaldataprocess.alDumpcollScp
import com.pharbers.aqll.common.alFileHandler.serverConfig._
import scala.concurrent.stm.atomic
import scala.concurrent.stm.Ref
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import com.pharbers.aqll.alCalaHelp.dbcores._

object alCalcActor {
    def props : Props = Props[alCalcActor]

    val core_number = server_info.cpu
}

class alCalcActor extends Actor 
                     with ActorLogging 
                     with FSM[alPointState, alCalcParmary]
                     with alCreateConcretCalcRouter
                     with alPkgJob {

    import alCalcActor.core_number
   
    startWith(alMasterJobIdle, new alCalcParmary("", ""))

	var maxProperty: alMaxProperty = null

    when(alMasterJobIdle) {
        case Event(can_sign_job(), _) => {
            sender() ! sign_job_can_accept()
            stay()
        }

        case Event(calc_job(p, parm), data) => {
	        maxProperty = p
            data.uuid = parm.uuid
            data.company = parm.company
            data.year = parm.year
            data.market = parm.market
            data.uname = parm.uname
            atomic { implicit tnx =>
                concert_ref() = Some(p)
	            log.info(s"calc finally $p")
            }

	        log.info(s"unCalcPkgSplit uuid = ${p.uuid}")

            cur = Some(new unPkgCmd(s"${root + scpPath + p.uuid}", s"${root + program}") :: Nil)
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
	                log.info("group no subs list")
                    stay()
            }
        }

        case Event(concert_calcjust_result(i), _) => {
            atomic { implicit tnx =>
                calcjust_index() = i
            }
            stay()
        }

        case Event(clean_crash_actor(uuid), data) => {
            val r = result_ref.single.get.find(x => x.parent == uuid)
            r match {
                case None => None
                case Some(d) =>
                    new alMessageProxy().sendMsg(s"文件在计算过程中崩溃，该文件UUID为:$uuid，请及时联系管理人员，协助解决！", data.uname, Map("type" -> "txt"))
                    d.subs.foreach (x => dbc.getCollection(x.uuid).drop())
                    context stop self
            }
            stay()
        }
    }

    when(calc_coreing) {
        case Event(calcing_job(lst, p), data) => {

            val m = lst.map (_.result.get.asInstanceOf[(String, List[String])]._2).flatten.distinct

            val q = m.map (x => alMaxProperty(p, x, Nil))
            atomic { implicit tnx =>
                result_ref() = Some(alMaxProperty(concert_ref.single.get.get.uuid, p, q))
                adjust_index() = -1
            }
            concert_router ! concert_adjust()
            context.watch(concert_router)

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

	        log.info(s"sub_uuid done $sub_uuid")

            if (r.subs.filterNot (x => x.isSumed).isEmpty) {
                r.isSumed = true
                val st = context.actorSelection(singletonPaht)

                r.subs.foreach { x =>
                    st ! calc_sum_result(r.parent, x.uuid, x.sum)
                }
            }
            stay()
        }
        case Event(calc_avg_job(uuid, avg), _) => {
            val r = result_ref.single.get.map (x => x).getOrElse(throw new Exception("must have runtime property"))

	        log.info(s"avg")
            
            if (r.parent == uuid)
                concert_router ! concert_calc_avg(r, avg)
            stay()
        }

        case Event(Terminated(a), data) => {
	        data.faultTimes = data.faultTimes + 1
	        if(data.faultTimes == data.maxTimeTry) {
		        log.info(s"concert_calc_avg -- 该UUID: ${data.uuid},在尝试性计算3次后，其中的某个线程计算失败，正在结束停止计算！")
                context.actorSelection(singletonPaht) ! crash_calc(data.uuid, "concert_calc_avg计算crash")
                context.unwatch(concert_router)
	        }else {
		        log.info(s"concert_calc_avg -- 尝试${data.faultTimes}次")
                self ! calc_job(maxProperty, data)
	        }
	        goto(alMasterJobIdle) using data
        }

        case Event(concert_calc_result(sub_uuid, v, u), data) => {
            val r = result_ref.single.get.map (x => x).getOrElse(throw new Exception("must have runtime property"))
            r.subs.find (x => x.uuid == sub_uuid).map { x =>
                x.isCalc = true
                x.finalValue = v
                x.finalUnit = u
            }.getOrElse(Unit)

            log.info(s"单个线程备份传输开始")
            alDumpcollScp().apply(sub_uuid, serverHost215)
	        log.info(s"单个线程备份传输结束")

	        log.info(s"单个线程开始删除临时表")
            dbc.getCollection(sub_uuid).drop()
	        log.info(s"单个线程结束删除临时表")
    
            new alMessageProxy().sendMsg("1", data.uname, Map("uuid" -> data.uuid, "company" -> data.company, "type" -> "progress"))

            if (r.subs.filterNot (x => x.isCalc).isEmpty) {
                r.isCalc = true

                val st = context.actorSelection(singletonPaht)

                r.subs.foreach { x =>
                    st ! calc_final_result(r.parent, x.uuid, x.finalValue, x.finalUnit)
                }
                goto(alMasterJobIdle) using new alCalcParmary("", "")

            } else stay()
        }
    }

    whenUnhandled {
        case Event(can_sign_job(), _) => {
            sender() ! service_is_busy()
            stay()
        }

        case Event(group_job(p, parm), _) => {
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

        case Event(Terminated(a), data) => {
	        data.faultTimes = data.faultTimes + 1
	        if(data.faultTimes == data.maxTimeTry) {
		        log.info(s"concert_calc -- 该UUID: ${data.uuid},在尝试性计算3次后，其中的某个线程计算失败，正在结束停止计算！")
                context.unwatch(concert_router)
                context.actorSelection(singletonPaht) ! crash_calc(data.uuid, "concert_calc计算crash")
	        }else {
                log.info(s"concert_calc -- 尝试${data.faultTimes}次")
		        self ! calc_job(maxProperty, data)
	        }
	        goto(alMasterJobIdle) using data
        }
    }

    val concert_ref : Ref[Option[alMaxProperty]] = Ref(None)            // 向上传递的，返回master的，相当于parent
    val result_ref : Ref[Option[alMaxProperty]] = Ref(None)             // 当前节点上计算的东西，相当于result
    val adjust_index = Ref(-1)
    val calcjust_index = Ref(-1)
    val concert_router = CreateConcretCalcRouter
}

trait alCreateConcretCalcRouter extends alCalcSupervisorStrategy { this : Actor =>
    import alCalcActor.core_number
    def CreateConcretCalcRouter =
        context.actorOf(BroadcastPool(core_number).props(alConcertCalcActor.props), name = "concert-calc-router")
}