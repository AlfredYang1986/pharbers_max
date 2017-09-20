package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.actor.{Actor, ActorLogging, Props}
import com.pharbers.aqll.alCalc.almain.alShareData
import com.pharbers.aqll.alCalcOther.alfinaldataprocess.alInertDatabase
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcData._

import scala.concurrent.stm.atomic
/**
  * Created by jeorch on 17-9-4.
  */

object alDoInsertDbComeo {
    def props = Props[alDoInsertDbComeo]
}

class alDoInsertDbComeo extends Actor with ActorLogging {

    var unit: Double = 0.0
    var value: Double = 0.0

    override def receive: Receive = {
        case do_insert_db(source, avg, sub_uuid, insert_sender, tmp) => {
            source.enumDataWithFunc { line =>
                val mrd = alShareData.txt2WestMedicineIncome2(line)
                avg.find(p => p._1 == mrd.segment).map { x =>
                    if (mrd.ifPanelAll.equals("1")) {
                        mrd.set_finalResultsValue(mrd.sumValue)
                        mrd.set_finalResultsUnit(mrd.volumeUnit)
                    } else {
                        mrd.set_finalResultsValue(BigDecimal((x._2 * mrd.selectvariablecalculation.get._2 * mrd.factor.toDouble).toString).toDouble)
                        mrd.set_finalResultsUnit(BigDecimal((x._3 * mrd.selectvariablecalculation.get._2 * mrd.factor.toDouble).toString).toDouble)
                    }

                }.getOrElse(Unit)
                unit = BigDecimal((unit + mrd.finalResultsUnit).toString).toDouble
                value = BigDecimal((value + mrd.finalResultsValue).toString).toDouble

                atomic { implicit thx =>
                    alInertDatabase().apply(mrd, sub_uuid)
                }
            }
            log.info(s"calc done at $sub_uuid")
//            sender ! after_insert_db()
            sender() ! calc_data_result(value, unit)
            sender() ! calc_data_end(true, tmp)
        }
    }
}
