import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alCalcParmary
import com.pharbers.aqll.alCalcMaster.alMasterTrait.alCameoFilterExcel.filter_excel_end
import com.pharbers.aqll.alCalcMaster.alMaxMaster
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger.filter_excel_job_2
import com.pharbers.aqll.common.alFileHandler.fileConfig.fileBase
import com.typesafe.config.ConfigFactory
import org.specs2.mutable.Specification

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by alfredyang on 11/07/2017.
  */
class alMaxMasterSpec extends Specification {

    val config = ConfigFactory.load("split-test")
    val system = ActorSystem("calc", config)
    implicit val timeout = Timeout(10 minute)

    override def is = s2"""
        This is a Max Master Specification to check the 'Max calc' process

            The 'Max master' functions should
                filter excel file "2016-01.xlsx"                        $e1
                                                                        """

    def e1 = {
        val a = system.actorOf(alMaxMaster.props)
        val path = fileBase + "2016-01.xlsx"
        val f = a ? filter_excel_job_2(path, new alCalcParmary("tekken", "alfred"))
        val r = Await.result(f, 10 minute).asInstanceOf[filter_excel_end].result
        r must_== true
    }
}
