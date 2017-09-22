package pfizer

import java.text.SimpleDateFormat
import java.util.Date

import akka.actor.ActorSystem
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxDriver
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxDriver.pushGeneratePanelJobs
import com.pharbers.aqll.alStart.alHttpFunc.alUploadItem
import org.scalatest.FunSuite

/**
  * Created by clock on 17-9-7.
  */
class PfizerSuite extends FunSuite {

    test("Pressure test => 50") {
        val dateformat1 = new SimpleDateFormat("MM-dd HH:mm:ss")
        println(s"压力测试开始时间" + dateformat1.format(new Date()))

        val gycx_file_local = "/home/clock/workSpace/blackMirror/dependence/program/generatePanel/file/Client/GYCX/1705 GYC.xlsx"
        val cpa_file_local = "/home/clock/workSpace/blackMirror/dependence/program/generatePanel/file/Client/CPA/1705 CPA.xlsx"

        val as = ActorSystem("testGeneratePanel")
        for(i <- 1 to 50) {
//            val args: Map[String, List[String]] = Map(
//                "company" -> List("generatePanel"),
//                "user" -> List("user"+i),
//                "cpas" -> List(cpa_file_local),
//                "gycxs" -> List(gycx_file_local)
//            )
            val gpActor = as.actorOf(alMaxDriver.props)
            gpActor ! pushGeneratePanelJobs(alUploadItem("generatePanel","user"+i,cpa_file_local,gycx_file_local,"201705"))

//            val parse = new phPfizerHandleImpl(args)
//            val ym = parse.calcYM.asInstanceOf[JsString].value
//            val result = parse.generatePanelFile(ym)
//            println(s"第$i 个完成结果 $result")
        }

        println(s"压力测试结束时间" + dateformat1.format(new Date()))
    }
}