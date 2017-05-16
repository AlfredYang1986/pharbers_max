package com.pharbers.aqll.alcalc.almain

import akka.actor.{Actor, ActorLogging, FSM, Props, Terminated}
import akka.routing.BroadcastPool
import com.pharbers.aqll.alcalc.aldata.alStorage
import com.pharbers.aqll.alcalc.alemchat.sendMessage
import com.pharbers.aqll.alcalc.aljobs.alJob.grouping_jobs
import com.pharbers.aqll.alcalc.aljobs.aljobstates.alMaxGroupJobStates.{group_coreing, group_doing}
import com.pharbers.aqll.alcalc.aljobs.aljobstates.{alMasterJobIdle, alPointState}
import com.pharbers.aqll.alcalc.aljobs.aljobtrigger.alJobTrigger.{concert_groupjust_result, _}
import com.pharbers.aqll.alcalc.almaxdefines.{alCalcParmary, alMaxProperty}
import com.pharbers.aqll.alcalc.alstages.alStage
import com.pharbers.aqll.alcalc.alprecess.alprecessdefines.alPrecessDefines._
import com.pharbers.aqll.alcalc.aljobs.alJob._
import com.pharbers.aqll.alcalc.aljobs.alPkgJob
import com.pharbers.aqll.alcalc.almodel.IntegratedData
import com.pharbers.aqll.alcalc.alprecess.alsplitstrategy.server_info
import com.pharbers.aqll.common.alCmd.pkgcmd.{pkgCmd, unPkgCmd}
import com.pharbers.aqll.common.alCmd.scpcmd.scpCmd
import com.pharbers.aqll.common.alDao._data_connection_cores
import com.pharbers.aqll.alcalc.alCommon.fileConfig._
import com.pharbers.aqll.alcalc.alCommon.clusterListenerConfig._

import scala.concurrent.stm.atomic
import scala.concurrent.stm.Ref
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * Created by BM on 11/03/2017.
  */

case class FileParentUuid(var uuid: String)

object alGroupActor {
    def props : Props = Props[alGroupActor]
    val core_number = server_info.cpu
}

class alGroupActor extends Actor
                     with ActorLogging
                     with FSM[alPointState, alCalcParmary]
                     with alCreateConcretGroupRouter
					 with alPkgJob{

    startWith(alMasterJobIdle, new alCalcParmary("", ""))

    var maxProperty: alMaxProperty = null

    when(alMasterJobIdle) {
        case Event(can_sign_job(), _) => {
            sender() ! sign_job_can_accept()
            stay()
        }

        case Event(group_job(p, parm), data) => {
            maxProperty = p
            data.uuid = parm.uuid
            data.company = parm.company
            data.year = parm.year
            data.market = parm.market
            data.uname = parm.uname
            atomic { implicit tnx =>
                concert_ref() = Some(p)
            }

            // TODO: 接收到Driver的信息后开始在各个机器上解压SCP过来的tar.gz文件，在开始group

            println(s"unPkgSplit uuid = ${p.uuid}")
            cur = Some(new unPkgCmd(s"/root/program/scp/${p.uuid}", "/root/program/") :: Nil)
            process = do_pkg() :: Nil
            super.excute()

            groupjust_index.single.get match {
                case s: Int if s <= (p.subs.size - 1) =>
                    val cj = grouping_jobs(Map(grouping_jobs.max_uuid -> p.uuid, grouping_jobs.group_uuid -> p.subs(s).uuid))
                    context.system.scheduler.scheduleOnce(0 seconds, self, grouping_job(cj))
                    goto(group_coreing) using data
                case _ =>
                    println("group no subs list")
                    stay()
            }
//            val cj = grouping_jobs(Map(grouping_jobs.max_uuid -> p.uuid, grouping_jobs.group_uuid -> p.subs(groupjust_index.single.get).uuid))
//            context.system.scheduler.scheduleOnce(0 seconds, self, grouping_job(cj))

        }
        case Event(concert_groupjust_result(i), _) => {
            atomic { implicit tnx =>
                groupjust_index() = i
            }
            stay()
        }

        case Event(clean_crash_actor(uuid), data) => {
            val r = result_ref.single.get.find(x => x.parent == uuid)
            r match {
                case None => None
                case Some(d) =>
                    sendMessage.sendMsg(s"文件在分组过程中崩溃，该文件UUID为:$uuid，请及时联系管理人员，协助解决！", data.uname, Map("type" -> "txt"))
                    d.subs.foreach (x => _data_connection_cores.getCollection(x.uuid).drop())
//                Restart
            }
            goto(alMasterJobIdle) using new alCalcParmary("", "")
        }
    }

    when(group_coreing) {
        case Event(grouping_job(cj), data) => {
            val result = cj.result
            val (p, sb) = result.get.asInstanceOf[(String, List[String])]
            val q = sb.map (x => alMaxProperty(p, x, Nil))
            atomic { implicit tnx =>
                result_ref() = Some(alMaxProperty(concert_ref.single.get.get.uuid, p, q))
                adjust_index() = -1
            }

	        concert_router ! concert_adjust()
            context.watch(concert_router)
            goto(group_doing) using data
        }
    }

    when(group_doing) {
        case Event(concert_group_result(sub_uuid), data) => {

            val r = result_ref.single.get.map (x => x).getOrElse(throw new Exception("must have runtime property"))
            
            r.subs.find (x => x.uuid == sub_uuid).map (x => x.grouped = true).getOrElse(Unit)

            if (r.subs.filterNot (x => x.grouped).isEmpty) {

                val common = common_jobs()
//                common.cur = Some(alStage(r.subs map (x => s"config/group/${x.uuid}")))
                common.cur = Some(alStage(r.subs map (x => s"${memorySplitFile}${group}${x.uuid}")))
                common.process = restore_grouped_data() ::
                                    do_calc() :: do_union() :: do_calc() ::
                                    do_map (alShareData.txt2IntegratedData(_)) :: do_calc() :: Nil
//                                    presist_data(Some(r.uuid), Some("group")) :: Nil
                common.result

                val concert = common.cur.get.storages.head.asInstanceOf[alStorage]
                val m = alStorage.groupBy (x =>
                    (x.asInstanceOf[IntegratedData].getYearAndmonth, x.asInstanceOf[IntegratedData].getMinimumUnitCh)
                )(concert)

                val g = alStorage(m.values.map (x => x.asInstanceOf[alStorage].data.head.toString).toList)
                g.doCalc
                val sg = alStage(g :: Nil)
                val pp = presist_data(Some(r.uuid), Some("group"))
                pp.precess(sg)

                println(s"post group result ${r.parent} && ${r.uuid}")

                // TODO : 把各个线程上汇总的Group文件再次tar.gz 传输到Driver机器上 进行最终的Group合并

                println(s"group sum uuid = ${r.uuid}")

                cur = Some(new pkgCmd(s"${memorySplitFile}${group}${r.uuid}" :: Nil, s"${memorySplitFile}${fileTarGz}${r.uuid}")
                    :: new scpCmd(s"${memorySplitFile}${fileTarGz}${r.uuid}.tar.gz", s"${program+scpPath}", "aliyun215", "root")
                    :: Nil)
                process = do_pkg() :: Nil
                super.excute()

                val st = context.actorSelection(singletonPaht)
                st ! group_result(r.parent, r.uuid)
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

        case Event(Terminated(a), data) => {
            data.faultTimes = data.faultTimes + 1
            if(data.faultTimes == data.maxTimeTry) {
                log.info(s"concert_group -- 该UUID: ${data.uuid},在尝试性group 3次后，其中的某个线程计算失败，正在结束停止计算！")
                context.actorSelection(singletonPaht) ! crash_calc(data.uuid, "concert_group crash")
                context.unwatch(concert_router)
            }else {
                log.info(s"concert_group -- 尝试${data.faultTimes}次")
                self ! calc_job(maxProperty, data)
            }
            goto(alMasterJobIdle) using data
        }
    }

    val concert_ref : Ref[Option[alMaxProperty]] = Ref(None)            // 向上传递的，返回master的，相当于parent
    val result_ref : Ref[Option[alMaxProperty]] = Ref(None)             // 当前节点上计算的东西，相当于result
    val adjust_index = Ref(-1)
    val groupjust_index = Ref(-1)
    val concert_router = CreateConcretGroupRouter
}

trait alCreateConcretGroupRouter extends alSupervisorStrategy { this : Actor =>
    import alCalcActor.core_number
    def CreateConcretGroupRouter =
        context.actorOf(BroadcastPool(core_number).props(alConcertGroupActor.props), name = "concert-group-router")
}