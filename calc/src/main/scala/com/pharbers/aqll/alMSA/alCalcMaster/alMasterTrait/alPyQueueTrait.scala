package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import akka.actor.Actor
import akka.agent.Agent
import com.pharbers.aqll.alCalcOther.alMessgae.alMessageProxy
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxDriver.{doPyUbJob, doPyUlJob, pyUbSchedule, pyUlSchedule}
import com.pharbers.aqll.alStart.alHttpFunc.{alUpBeforeItem, alUploadItem}
import com.pharbers.aqll.common.alCmd.pycmd.pyCmd
import com.pharbers.aqll.common.alFileHandler.fileConfig._
import scala.concurrent.duration._
import scala.concurrent.stm._

/**
  * Created by jeorch on 17-8-31.
  */
trait alPyQueueTrait { this : Actor =>

    var py_ub_jobs = Ref(List[alUpBeforeItem]())
    var py_ul_jobs = Ref(List[alUploadItem]())

    import scala.concurrent.ExecutionContext.Implicits.global
    case class py_energy(val energy : Int)
    val pyLimit = Agent(py_energy(4))

    val py_ub_schedule = context.system.scheduler.schedule(1 second, 1 second, self, pyUbSchedule())
    val py_ul_schedule = context.system.scheduler.schedule(1 second, 1 second, self, pyUlSchedule())

    def push_py_ub_jobs(item : alUpBeforeItem) = {
        atomic { implicit thx =>
            py_ub_jobs() = py_ub_jobs() :+ item
        }
    }
    def push_py_ul_jobs(item : alUploadItem) = {
        atomic { implicit thx =>
            py_ul_jobs() = py_ul_jobs() :+ item
        }
    }
    def py_ub_schedule_jobs = {
        if (pyLimit().energy > 0) {
            atomic { implicit thx =>
                val tmp = py_ub_jobs.single.get
                if (tmp.isEmpty) Unit
                else {
//                    println(s"py_limit=${pyLimit().energy}")
                    pyLimit send py_energy(pyLimit().energy - 1)
                    py_ub_jobs() = py_ub_jobs().tail
                    self ! doPyUbJob(tmp.head)
//                    println(s"执行py_ub_job!${tmp.head}")
                }
            }
        }
    }
    def py_ul_schedule_jobs = {
        if (pyLimit().energy > 0) {
            atomic { implicit thx =>
                val tmp = py_ul_jobs.single.get
                if (tmp.isEmpty) Unit
                else {
//                    println(s"py_limit=${pyLimit().energy}")
                    pyLimit send py_energy(pyLimit().energy - 1)
                    py_ul_jobs() = py_ul_jobs().tail
                    self ! doPyUlJob(tmp.head)
//                    println(s"执行py_ul_job!${tmp.head}")
                }
            }
        }
    }
    def do_py_ub_job(item : alUpBeforeItem) = {
        val result = pyCmd(s"$fileBase${item.company}" ,Upload_Firststep_Filename, "").excute
        alMessageProxy().sendMsg("100", item.uname, Map("uuid" -> "", "company" -> item.company, "type" -> "progress"))
        pyLimit send py_energy(pyLimit().energy + 1)
    }
    def do_py_ul_job(item : alUploadItem) = {
        val result = pyCmd(s"$fileBase${item.company}",Upload_Secondstep_Filename, item.yms).excute
        pyLimit send py_energy(pyLimit().energy + 1)
    }
}
