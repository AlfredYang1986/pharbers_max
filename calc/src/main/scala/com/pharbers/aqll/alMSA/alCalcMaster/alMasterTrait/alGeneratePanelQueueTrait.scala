package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import akka.actor.{Actor, ActorLogging, Props}
import akka.agent.Agent
import com.pharbers.aqll.alCalcOther.alMessgae.alMessageProxy
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxDriver._
import com.pharbers.aqll.alStart.alHttpFunc.{alUpBeforeItem, alUploadItem}
import com.pharbers.pfizer.impl.phPfizerHandleImpl
import play.api.libs.json.JsString

import scala.collection.immutable.Map
import scala.concurrent.duration._
import scala.concurrent.stm._

/**
  * Created by jeorch on 17-8-31.
  */
trait alGeneratePanelQueueTrait { this : Actor =>

    val calc_ym_jobs = Ref(List[alUpBeforeItem]())
    val generate_panel_jobs = Ref(List[alUploadItem]())

    import scala.concurrent.ExecutionContext.Implicits.global
    val pyLimit = Ref(4)

    val calc_ym_schedule = context.system.scheduler.schedule(1 second, 1 second, self, calcYMSchedule())
    val generate_panel_schedule = context.system.scheduler.schedule(1 second, 1 second, self, generatePanelSchedule())

    def push_calc_ym_jobs(item : alUpBeforeItem) = {
        atomic { implicit thx =>
            calc_ym_jobs() = calc_ym_jobs() :+ item
        }
    }
    def push_generate_panel_jobs(item : alUploadItem) = {
        atomic { implicit thx =>
            generate_panel_jobs() = generate_panel_jobs() :+ item
        }
    }
    def calc_ym_schedule_jobs = {
        if (pyLimit.single.get > 0) {
            atomic { implicit thx =>
                val tmp = calc_ym_jobs.single.get
                if (tmp.isEmpty) Unit
                else {
                    pyLimit() = pyLimit.single.get - 1
                    calc_ym_jobs() = calc_ym_jobs().tail
                    do_calc_ym_job(tmp.head)
                }
            }
        }
    }
    def generate_panel_schedule_jobs = {
        if (pyLimit.single.get > 0) {
            atomic { implicit thx =>
                val tmp = generate_panel_jobs.single.get
                if (tmp.isEmpty) Unit
                else {
                    pyLimit() = pyLimit.single.get - 1
                    generate_panel_jobs() = generate_panel_jobs().tail
                    do_generate_panel_job(tmp.head)
                }
            }
        }
    }
    def do_calc_ym_job(item : alUpBeforeItem) = {
        val act = context.actorOf(alPanelJobComeo.props)
        act ! calcYMJob(item)
    }
    def do_generate_panel_job(item : alUploadItem) = {
        val act = context.actorOf(alPanelJobComeo.props)
        act ! generatePanelJob(item)
    }
    def release_py_energy = {
        atomic { implicit thx =>
            pyLimit() = pyLimit.single.get + 1
        }
    }

}
object alPanelJobComeo {
    def props = Props[alPanelJobComeo]
}

class alPanelJobComeo extends Actor with ActorLogging {

    override def receive: Receive = {
        case calcYMJob(item) => calcYM(item)
        case generatePanelJob(item) => generatePanel(item)
        case _ => Unit
    }

    def calcYM(item: alUpBeforeItem) = {
        val args: Map[String, List[String]] = Map(
            "company" -> List(item.company),
            "user" -> List(item.user),
            "cpas" -> item.cpas.split("&").toList,
            "gycxs" -> item.gycxs.split("&").toList
        )
        val result = new phPfizerHandleImpl(args).calcYM.asInstanceOf[JsString].value
        alMessageProxy().sendMsg(result, item.user, Map("type" -> "txt"))
        sender ! releasePyEnergy()
    }

    def generatePanel(item: alUploadItem) = {
        val args: Map[String, List[String]] = Map(
            "company" -> List(item.company),
            "user" -> List(item.user),
            "cpas" -> item.cpas.split("&").toList,
            "gycxs" -> item.gycxs.split("&").toList
        )
        val result = new phPfizerHandleImpl(args).generatePanelFile(item.ym).asInstanceOf[JsString].value
        alMessageProxy().sendMsg(result, item.user, Map("type" -> "txt"))
        sender ! releasePyEnergy()
    }

}
