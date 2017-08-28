package com.pharbers.aqll.alCalcMemory.aljobs

import akka.actor.{Actor, ActorSystem}
import com.pharbers.aqll.alCalc.almain.alShareData
import com.pharbers.aqll.alCalcMemory.alprecess.alprecessdefines.alPrecessDefines._
import com.pharbers.aqll.alCalcMemory.alprecess.alsplitstrategy.alSplitStrategy._
import com.pharbers.aqll.alCalcMemory.alprecess.alsplitstrategy.server_info
import com.pharbers.aqll.alCalcMemory.alstages.alStage
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.queryIdleNodeInstanceInSystemWithRole
import com.pharbers.aqll.common.alFileHandler.fileConfig._
import akka.pattern.ask

import scala.concurrent.Await
import scala.concurrent.duration._
import akka.util.Timeout
import com.pharbers.aqll.alStart.alHttpFunc.alAkkaSystemGloble


class alSplitGroupDataJob(u : String) extends alJob {
    implicit val timeout = Timeout(2 seconds)
    override val uuid: String = u
    val ps = presist_data(Some(uuid), Some("calc"))

    def init(args : Map[String, Any]) = {
        val restore_path = s"${memorySplitFile + group + uuid}"
        cur = Some(alStage(restore_path))
        process = restore_grouped_data() :: split_data(hash_split(Map(hash_split.core_number-> server_info.cpu,
                                                                      hash_split.mechine_number -> server_info.section.single.get, //query(), //server_info.section.single.get,
                                                                      hash_split.hash_func -> hash_func))) :: ps :: Nil
    }
    
    def query() = {
        val r =  alAkkaSystemGloble.system.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception") ? queryIdleNodeInstanceInSystemWithRole("splitgroupslave")
        val result = Await.result(r, timeout.duration).asInstanceOf[Int]
        println(s"result ===---===>>> ${result}")
        result
    }
    
    override def result : Option[Any] =  {
        super.result
        ps.result
    }

    val hash_func : Any => Int = { x =>
        val d = alShareData.txt2IntegratedData(x.asInstanceOf[String])
        (d.getYearAndmonth.toString + d.getMinimumUnitCh).toStream.map (c => c.toInt).sum
    }
}