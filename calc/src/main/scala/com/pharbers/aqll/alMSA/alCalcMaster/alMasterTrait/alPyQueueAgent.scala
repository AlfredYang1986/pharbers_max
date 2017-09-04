package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import akka.actor.{Actor, ActorLogging, Props}
import com.pharbers.aqll.alStart.alHttpFunc.{alUpBeforeItem, alUploadItem}

/**
  * Created by jeorch on 17-9-4.
  */

object alPyQueueAgent {
    def props = Props[alPyQueueAgent]
    def name = "python-agent"

    case class pushPyUbJobs(item : alUpBeforeItem)
    case class pushPyUlJobs(item : alUploadItem)
    case class pyUbSchedule()
    case class pyUlSchedule()
    case class doPyUbJob(item : alUpBeforeItem)
    case class doPyUlJob(item : alUploadItem)
}

class alPyQueueAgent extends Actor with ActorLogging
    with alPyQueueTrait{

    import alPyQueueAgent._

    override def receive: Receive = {
        case pushPyUbJobs(item) => push_py_ub_jobs(item)
        case pushPyUlJobs(item) => push_py_ul_jobs(item)
        case pyUbSchedule() => py_ub_schedule_jobs
        case pyUlSchedule() => py_ul_schedule_jobs
        case doPyUbJob(item) => do_py_ub_job(item)
        case doPyUlJob(item) => do_py_ul_job(item)

        case _ => ???
    }
}