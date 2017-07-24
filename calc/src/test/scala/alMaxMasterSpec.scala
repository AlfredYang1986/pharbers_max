import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import com.pharbers.aqll.alCalaHelp.alMaxDefines.{alCalcParmary, alMaxProperty}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoFilterExcel.filter_excel_end
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger._
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.queryIdleNodeInstanceInSystemWithRole
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcData.calc_data_end
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoGroupData.group_data_end
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoSplitExcel.split_excel_end
import com.pharbers.aqll.common.alFileHandler.fileConfig.fileBase
import com.typesafe.config.ConfigFactory
import org.specs2.mutable.Specification
import org.specs2.specification.AfterAll

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by alfredyang on 11/07/2017.
  */
class alMaxMasterSpec extends Specification with AfterAll{

    val config = ConfigFactory.load("split-test")
    val system : ActorSystem = ActorSystem("calc", config)
    implicit val timeout = Timeout(30 minute)

    val cp = new alCalcParmary("tekken", "alfred")

    override def afterAll() : Unit = {
        system.terminate()
        Await.result(system.whenTerminated, Duration.Inf)
        println("wokao")
    }

    override def is = s2"""
        This is a Max Master Specification to check the 'Max calc' process
            The 'Max master' structure should

            The 'Max master' functions should
                calc data                                               $e5

                                                                        """
//    1 master 1 filter slave                                 $e1
//    filter excel file "2016-01.xlsx"                        $e2
//    split excel file "2016-01.xlsx"                         $e3
//    group integrated data                                   $e4

    def e1 = {
        val a = system.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
        val f1 = a ? queryIdleNodeInstanceInSystemWithRole("splitmaster")
        val f1r = Await.result(f1, 2 second).asInstanceOf[Int]

        val f2 = a ? queryIdleNodeInstanceInSystemWithRole("splitfilterexcelslave")
        val f2r = Await.result(f2, 2 second).asInstanceOf[Int]

        val f3 = a ? queryIdleNodeInstanceInSystemWithRole("splitsplitexcelslave")
        val f3r = Await.result(f3, 2 second).asInstanceOf[Int]

        val f4 = a ? queryIdleNodeInstanceInSystemWithRole("splitgroupslave")
        val f4r = Await.result(f4, 2 second).asInstanceOf[Int]

        val f5 = a ? queryIdleNodeInstanceInSystemWithRole("splitcalcslave")
        val f5r = Await.result(f5, 2 second).asInstanceOf[Int]

        f1r must_== 1
        f2r must_== 1
        f3r must_== 1
        f4r must_== 2
        f5r must_== 0
    }

    def e2 = {
        val a = system.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/driver-actor")
        val path = fileBase + "2016-01.xlsx"
        val f = a ? filter_excel_job_2(path, cp)
        val r = Await.result(f, 2 minute).asInstanceOf[filter_excel_end].result
        r must_== true
    }

    def e3 = {
        val a = system.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/driver-actor")
        val path = fileBase + "2016-01.xlsx"
        val f = a ? push_split_excel_job(path, cp)
        val r = Await.result(f, 2 minute).asInstanceOf[split_excel_end]

        r.result must_== true
    }

    def e4 = {
        val a = system.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/driver-actor")
        val path = fileBase + "2016-01.xlsx"
        val f = a ? push_split_excel_job(path, cp)
        val r = Await.result(f, 2 minute).asInstanceOf[split_excel_end]

        val p = r.uuid
        val subs = r.subs map (x => alMaxProperty(p, x, Nil))
        val mp = alMaxProperty(null, p, subs)
        println(mp)

        r.result must_== true
        cp.uuid = r.uuid
        mp must_!= null

        val fg = a ? push_group_job(mp)
        val rg = Await.result(fg, 2 minute).asInstanceOf[group_data_end]

        println(rg.property)
        rg.result must_== true
    }

    def e5 = {
        val a = system.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/driver-actor")
        val path = fileBase + "2016-01.xlsx"
        val f = a ? push_split_excel_job(path, cp)
        val r = Await.result(f, 2 minute).asInstanceOf[split_excel_end]

        val p = r.uuid
        val subs = r.subs map (x => alMaxProperty(p, x, Nil))
        val mp = alMaxProperty(null, p, subs)
        println(mp)

        r.result must_== true
        cp.uuid = r.uuid
        mp must_!= null

        val fg = a ? push_group_job(mp)
        val rg = Await.result(fg, 2 minute).asInstanceOf[group_data_end]

        println(rg.property)
        rg.result must_== true

        val fff = a ? push_calc_job_2(mp, cp)
        val rrr = Await.result(fff, 30 minute).asInstanceOf[calc_data_end]

        println(rrr.property.finalValue)
        println(rrr.property.finalUnit)
        rrr.result must_== true
    }
}
