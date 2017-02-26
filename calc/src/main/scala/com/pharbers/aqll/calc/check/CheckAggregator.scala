package com.pharbers.aqll.calc.check

import akka.actor.{Actor, ActorRef, Props}
import com.pharbers.aqll.calc.split.SplitEventBus

import scala.concurrent.stm.Ref
import scala.concurrent.stm.atomic

/**
  * Created by Faiz on 2017/1/4.
  */

object CheckAggregator {
	def props(bus: SplitEventBus, master: ActorRef) = Props(new CheckAggregator(bus, master))

	case class aggsubcribe(a: ActorRef)

	case class excelResult(exd: (Double, Double, Int, List[(String)], List[(String)], List[(String)]))

}

class CheckAggregator(bus: SplitEventBus, master: ActorRef) extends Actor {
	val excelcheckdata = Ref(List[(Double, Double, Long, String, String, Long)]())
	val excelchecksize = Ref(0)
	val excelshouleszie = Ref(0)

	def receive = {
		case CheckAggregator.aggsubcribe(a) => {
			atomic { implicit thx =>
				excelshouleszie() = excelshouleszie() + 1
			}
			bus.subscribe(a, "AggregorBus")
		}
		case CheckWorker.exceluniondata(excel, hospmatchpath) => {
			import com.pharbers.aqll.calc.common.DefaultData
			atomic { implicit thx =>
				excelchecksize() = excelchecksize() + 1
				excelcheckdata() = excelcheckdata() ++: excel
			}
			println(s"excelchecksize.single.get == ${excelchecksize.single.get}")
			if (excelchecksize.single.get == 10) {
				val temp = excelcheckdata.single.get
				val hospnumdist = temp.map(_._6).distinct.sortBy(x => x)
				val hospmatch = DefaultData.hospmatchdata(hospmatchpath).map(x => x.getHospNum.toLong).sortBy(x => x).map { x =>
					if (hospnumdist.exists(z => x == z)) x else None
				}.filter(_ != None)
				val hospnomatch = DefaultData.hospmatchdata(hospmatchpath).map(x => (x.getHospNum.toLong, x.getHospNameCh)).sortBy(x => x._1).map { x =>
					if (!hospmatch.exists(z => x._1 == z)) x._2 else None
				}.filter(_ != None).asInstanceOf[List[String]]
				val tmp = (temp.map(_._1).sum,
					temp.map(_._2).sum,
					hospnumdist.size,
					temp.map(_._5).distinct,
					temp.map(_._4).distinct,
					hospnomatch)
				master ! CheckAggregator.excelResult(tmp)
			}
		}
	}
}
