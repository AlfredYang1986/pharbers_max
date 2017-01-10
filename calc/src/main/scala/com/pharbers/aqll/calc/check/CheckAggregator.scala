package scala.com.pharbers.aqll.calc.check

import akka.actor.{Actor, ActorRef, Props}
import com.pharbers.aqll.calc.split.SplitEventBus

import scala.concurrent.stm.Ref
import scala.concurrent.stm.atomic

/**
  * Created by Faiz on 2017/1/4.
  */

object CheckAggregator {
    def props(bus : SplitEventBus, master : ActorRef) = Props(new CheckAggregator(bus, master))
    case class aggsubcribe(a : ActorRef)
    case class excelResult(exd: (Double, Double, Int, List[(String)], List[(String)]))
}

class CheckAggregator(bus : SplitEventBus, master : ActorRef)extends Actor {
    val excelcheckdata = Ref(List[(Double, Double, Long, String)]())
    val excelchecksize = Ref(0)
    val excelshouleszie = Ref(0)

    def receive = {
        case CheckAggregator.aggsubcribe(a) => {
            atomic { implicit thx =>
                excelshouleszie() = excelshouleszie() + 1
            }
            println(s"worker ExcelCheck should size ${excelshouleszie.single.get}")
            bus.subscribe(a, "AggregorBus")
        }
        case CheckWorker.exceluniondata(excel) => {
            import com.pharbers.aqll.calc.common.DefaultData
            atomic { implicit thx =>
                excelchecksize() = excelchecksize() + 1
                excelcheckdata() = excelcheckdata() ++: excel
            }

            if (excelchecksize.single.get == 10) {
                val temp = excelcheckdata.single.get
                val t = temp.map(_._3).distinct.sortBy(x => x)
                val hospNum = DefaultData.hospmatchdata.map(x => (x.getHospNum.toLong, x.getHospNameCh)).sortBy(x => x).map { x =>
                    if(!t.exists ( z => x._1 == z )) x._2 else ""
                }.filter(!_.equals(""))
                val tmp = (temp.map(_._1).sum,
                  temp.map(_._2).sum,
                  t.size,
                  hospNum,
                  temp.map(_._4).distinct)
                master ! CheckAggregator.excelResult(tmp)
            }
        }
    }
}
