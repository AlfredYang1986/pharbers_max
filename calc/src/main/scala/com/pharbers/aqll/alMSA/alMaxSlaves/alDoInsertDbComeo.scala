package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.actor.{Actor, ActorLogging, Props}
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alMaxProperty
import com.pharbers.aqll.alCalaHelp.dbcores.dbc
import com.pharbers.aqll.alCalc.almain.alShareData
import com.pharbers.aqll.alCalcOther.alfinaldataprocess.{alDumpcollScp, alInertDatabase}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcData._
import com.pharbers.aqll.common.alEncryption.alEncryptionOpt
import com.pharbers.aqll.common.alFileHandler.serverConfig.serverHost215

import scala.concurrent.stm.atomic
import scala.math.BigDecimal
/**
  * Created by jeorch on 17-9-4.
  */

object alDoInsertDbComeo {
    def props: Props = Props[alDoInsertDbComeo]
}

class alDoInsertDbComeo extends Actor with ActorLogging {

    var unit: Double = 0.0
    var value: Double = 0.0

    override def receive: Receive = {
        case do_insert_db(source, avg, sub_uuid, tmp) => {
            source.enumDataWithFunc { line =>

                val mrd = alShareData.txt2WestMedicineIncome2(line)
                val sheed = mrd.segment + mrd.minimumUnitCh + mrd.yearAndmonth.toString

                if (mrd.ifPanelAll == "1") {
                    mrd.set_finalResultsValue(mrd.sumValue)
                    mrd.set_finalResultsUnit(mrd.volumeUnit)
                }else {
                    avg.find(p => p._1 == alEncryptionOpt.md5(sheed).toString).map { x =>
                        mrd.set_finalResultsValue(BigDecimal((x._2 * mrd.selectvariablecalculation.get._2 * mrd.factor.toDouble).toString).toDouble)
                        mrd.set_finalResultsUnit(BigDecimal((x._3 * mrd.selectvariablecalculation.get._2 * mrd.factor.toDouble).toString).toDouble)
                    }.getOrElse(Unit)
                }

                unit = BigDecimal((unit + mrd.finalResultsUnit).toString).toDouble
                value = BigDecimal((value + mrd.finalResultsValue).toString).toDouble

                atomic { implicit thx =>
                    alInertDatabase().apply(mrd, sub_uuid)
                }

            }
            insertDbWithDrop(tmp)
            log.info(s"calc done at $sub_uuid")
            sender ! calc_data_result(value, unit)
            sender ! calc_data_end(true, tmp)
//            context stop self
        }
    }
    
    def insertDbWithDrop(p: alMaxProperty) = {
        log.info(s"单个线程备份传输开始")
        alDumpcollScp().apply(p.subs.head.uuid, serverHost215)
        log.info(s"单个线程备份传输结束")
        
        log.info(s"单个线程开始删除临时表")
        dbc.getCollection(p.subs.head.uuid).drop()
        log.info(s"单个线程结束删除临时表")
        
    }
}
