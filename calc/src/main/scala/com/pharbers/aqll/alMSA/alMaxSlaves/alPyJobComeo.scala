package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.actor.{Actor, ActorLogging, Props}
import com.pharbers.aqll.alCalcOther.alMessgae.alMessageProxy
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxDriver.{doPyUbJob, doPyUlJob, releasePyEnergy}
import com.pharbers.aqll.common.alCmd.pycmd.pyCmd
import com.pharbers.aqll.common.alFileHandler.fileConfig._

/**
  * Created by jeorch on 17-9-4.
  */

object alPyJobComeo {
    def props = Props[alPyJobComeo]
}

class alPyJobComeo extends Actor with ActorLogging {

    override def receive: Receive = {
        case doPyUbJob(item) => {
            val result = pyCmd(s"$fileBase${item.company}" ,Upload_Firststep_Filename, "").excute
            alMessageProxy().sendMsg("100", item.uname, Map("uuid" -> "", "company" -> item.company, "type" -> "progress"))
            sender ! releasePyEnergy
        }
        case doPyUlJob(item) => {
            val result = pyCmd(s"$fileBase${item.company}",Upload_Secondstep_Filename, item.yms).excute
            sender ! releasePyEnergy
        }
    }

}
