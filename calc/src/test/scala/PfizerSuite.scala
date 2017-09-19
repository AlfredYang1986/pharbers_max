import akka.actor.ActorSystem
import com.pharbers.pfizer.impl.phPfizerHandleImpl
import com.pharbers.pfizer.phPfizerHandleTrait
import org.scalatest.FunSuite

import scala.collection.immutable.Map

/**
  * Created by clock on 17-9-19.
  */
class PfizerSuite extends FunSuite {

    test("pfizer create panel file") {
        val cpa_file_local = "/home/clock/workSpace/blackMirror/dependence/program/generatePanel/file/Client/CPA/1705 CPA.xlsx"
        implicit val system = ActorSystem("phDataParse")
        val gycx_file_local = "/home/clock/workSpace/blackMirror/dependence/program/generatePanel/file/Client/GYCX/1705 GYC.xlsx"

        val args: Map[String, List[String]] = Map(
            "company" -> List("company"),
            "ym" -> List("201705"),
            "cpas" -> List(cpa_file_local),
            "gycxs" -> List(gycx_file_local)
        )

        val phfizerParse: phPfizerHandleTrait = new phPfizerHandleImpl()
        val result = phfizerParse.generatePanelFile(args)

        println(result)
    }
}
