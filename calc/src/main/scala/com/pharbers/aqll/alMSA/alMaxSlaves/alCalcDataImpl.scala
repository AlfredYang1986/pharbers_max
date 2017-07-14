package com.pharbers.aqll.alMSA.alMaxSlaves

import java.util.UUID

import akka.actor.{Actor, ActorLogging, Props}
import com.pharbers.aqll.alCalaHelp.alMaxDefines.{alCalcParmary, alMaxProperty}
import com.pharbers.aqll.alCalc.almain.alShareData
import com.pharbers.aqll.alCalc.almodel.java.IntegratedData
import com.pharbers.aqll.alCalc.almodel.scala.westMedicineIncome
import com.pharbers.aqll.alCalcMemory.aldata.alStorage
import com.pharbers.aqll.alCalcMemory.aljobs.alJob.{common_jobs, worker_calc_core_split_jobs, worker_core_calc_jobs}
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger.concert_calc_result
import com.pharbers.aqll.alCalcMemory.alprecess.alprecessdefines.alPrecessDefines._
import com.pharbers.aqll.alCalcMemory.alstages.alStage
import com.pharbers.aqll.alCalcOther.alfinaldataprocess.alInertDatabase
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcData._
import com.pharbers.aqll.common.alEncryption.alEncryptionOpt
import com.pharbers.aqll.common.alFileHandler.alFilesOpt.alFileOpt
import com.pharbers.aqll.common.alFileHandler.fileConfig.{calc, memorySplitFile, sync}

import scala.concurrent.stm.atomic

/**
  * Created by alfredyang on 13/07/2017.
  */

object alCalcDataImpl {
    def props = Props[alCalcDataImpl]
}

class alCalcDataImpl extends Actor with ActorLogging {

//    var uuid : String = ""
    var unit: Double = 0.0
	var value: Double = 0.0
	val maxSum: scala.collection.mutable.Map[String, (Double, Double, Double)] = scala.collection.mutable.Map.empty

    var tmp : alMaxProperty = null

    override def receive: Receive = {
        case calc_data_hand() => sender ! calc_data_hand()
        case calc_data_start_impl(p, c) => {
            tmp = p
            val cj = worker_core_calc_jobs(Map(worker_core_calc_jobs.max_uuid -> p.uuid, worker_core_calc_jobs.calc_uuid -> p.subs.head.uuid))
            cj.result

            val concert = cj.cur.get.storages.head.asInstanceOf[alStorage]

            val recall = resignIntegratedData(p.parent)(concert)
            concert.data.zipWithIndex.foreach { x =>
                max_precess(x._1.asInstanceOf[IntegratedData],
                    p.subs.head.uuid,
                    Some(x._2 + "/" + concert.data.length))(recall)(c)
            }

            log.info(s"concert uuid ${p.subs.head.uuid} end")
            val s = (maxSum.toList.groupBy(_._1) map { x =>
                (x._1, (x._2.map(z => z._2._1).sum, x._2.map(z => z._2._2).sum, x._2.map(z => z._2._3).sum))
            }).toList

            println(s"calcing sum is ${s}")
            sender ! calc_data_sum(s)
//            sender ! calc_data_end(true, p)
        }
        case calc_data_average(avg) => {
            import scala.math.BigDecimal

            println("avg start")
            val sub_uuid = tmp.subs.head.uuid
//            val path = s"${memorySplitFile}${calc}$sub_uuid"
            val path = "config/calc/" + sub_uuid
            val dir = alFileOpt(path)
            if (!dir.isExists)
                dir.createDir

            val source = alFileOpt(path + "/" + "data")
            if (source.isExists) {
                source.enumDataWithFunc { line =>
                    val mrd = alShareData.txt2WestMedicineIncome2(line)

                    avg.find(p => p._1 == mrd.segment).map { x =>
                        if (mrd.ifPanelAll.equals("1")) {
                            // mrd.set_finalResultsValue(BigDecimal(mrd.sumValue.toString).toDouble)
                            // mrd.set_finalResultsUnit(BigDecimal(mrd.volumeUnit.toString).toDouble)
                            mrd.set_finalResultsValue(mrd.sumValue)
                            mrd.set_finalResultsUnit(mrd.volumeUnit)
                        } else {

                            mrd.set_finalResultsValue(BigDecimal((x._2 * mrd.selectvariablecalculation.get._2 * mrd.factor.toDouble).toString).toDouble)
                            mrd.set_finalResultsUnit(BigDecimal((x._3 * mrd.selectvariablecalculation.get._2 * mrd.factor.toDouble).toString).toDouble)

                            // mrd.set_finalResultsValue(x._2 * mrd.selectvariablecalculation.get._2 * mrd.factor.toDouble)
                            // mrd.set_finalResultsUnit(x._3 * mrd.selectvariablecalculation.get._2 * mrd.factor.toDouble)
                        }

                    }.getOrElse(Unit)

                    unit = BigDecimal((unit + mrd.finalResultsUnit).toString).toDouble
                    value = BigDecimal((value + mrd.finalResultsValue).toString).toDouble

//                    atomic { implicit thx =>
//                        alInertDatabase().apply(mrd, sub_uuid)
//                    }
                }
                log.info(s"calc done at $sub_uuid")
            }

            println(s"value is $value")
            println(s"unit is $unit")

            sender ! calc_data_result(value, unit)
            sender ! calc_data_end(true, tmp)
        }
    }
    def max_precess(element2: IntegratedData, sub_uuid: String, longPath: Option[String] = None)(recall: List[IntegratedData])(c: alCalcParmary) = {
        if (!longPath.isEmpty) {
            log.info(s"concert calc in $longPath")
            val universe = alEncryptionOpt.md5(c.company + c.year + c.market)
            val tmp =
                alShareData.hospdata(universe, c.company) map { element =>
                    val mrd = westMedicineIncome(element.getCompany, element2.getYearAndmonth, 0.0, 0.0, element2.getMinimumUnit,
                        element2.getMinimumUnitCh, element2.getMinimumUnitEn, element2.getMarket1Ch,
                        element2.getMarket1En, element.getSegment, element.getFactor, element.getIfPanelAll,
                        element.getIfPanelTouse, element.getHospId, element.getHospName, element.getPhaid,
                        element.getIfCounty, element.getHospLevel, element.getRegion, element.getProvince,
                        element.getPrefecture, element.getCityTier, element.getSpecialty1, element.getSpecialty2,
                        element.getReSpecialty, element.getSpecialty3, element.getWestMedicineIncome, element.getDoctorNum,
                        element.getBedNum, element.getGeneralBedNum, element.getMedicineBedNum, element.getSurgeryBedNum,
                        element.getOphthalmologyBedNum, element.getYearDiagnosisNum, element.getClinicNum, element.getMedicineNum,
                        element.getSurgeryNum, element.getHospitalizedNum, element.getHospitalizedOpsNum, element.getIncome,
                        element.getClinicIncome, element.getClimicCureIncome, element.getHospitalizedIncome,
                        element.getHospitalizedBeiIncome, element.getHospitalizedCireIncom, element.getHospitalizedOpsIncome,
                        element.getDrugIncome, element.getClimicDrugIncome, element.getClimicWestenIncome,
                        element.getHospitalizedDrugIncome, element.getHospitalizedWestenIncome, 0.0, 0.0)
                    backfireData(mrd)(recall)
                }

//            val path = s"${memorySplitFile}${calc}$sub_uuid"
            val path = "config/calc/" + sub_uuid
            val dir = alFileOpt(path)
            if (!dir.isExists)
                dir.createDir

            val file = alFileOpt(path + "/" + "data")
            if (!file.isExists)
                file.createFile

            file.appendData2File(tmp)
        }
    }

    def resignIntegratedData(parend_uuid: String)(group: alStorage): List[IntegratedData] = {
        val recall = common_jobs()
//        val path = s"${memorySplitFile}${sync}$parend_uuid"
        val path = "config/sync/" + parend_uuid
        recall.cur = Some(alStage(alFileOpt(path).exHideListFile))
        recall.process = restore_data() :: do_calc() :: do_union() ::
            do_map(alShareData.txt2IntegratedData(_)) :: do_filter { iter =>
            val t = iter.asInstanceOf[IntegratedData]
            group.data.exists { g => true
                val x = g.asInstanceOf[IntegratedData]
                (x.getYearAndmonth == t.getYearAndmonth) && (x.getMinimumUnitCh == t.getMinimumUnitCh)
            }
        } :: do_calc() :: Nil
        recall.result
        log.info(s"current recall data length ${recall.cur.get.length}")
        recall.cur.get.storages.head.asInstanceOf[alStorage].data.asInstanceOf[List[IntegratedData]]
    }

    def backfireData(mrd: westMedicineIncome)(inte_lst: List[IntegratedData]): westMedicineIncome = {
        var t = mrd
        val tmp = inte_lst.find(iter => mrd.yearAndmonth == iter.getYearAndmonth
            && mrd.minimumUnitCh == iter.getMinimumUnitCh
            && mrd.phaid == iter.getPhaid)

        tmp match {
            case Some(x) => {
                mrd.set_sumValue(x.getSumValue)
                mrd.set_volumeUnit(x.getVolumeUnit)
            }
            case None => Unit
        }

        if (mrd.ifPanelTouse == "1") {
            maxSum += mrd.segment ->
                maxSum.find(p => p._1 == mrd.segment)
                    .map { x =>
                        (x._2._1 + mrd.sumValue, x._2._2 + mrd.volumeUnit, x._2._3 + mrd.selectvariablecalculation.get._2)
                    }
                    .getOrElse((mrd.sumValue, mrd.volumeUnit, mrd.selectvariablecalculation.get._2))
        }
        mrd.copy()
    }
}
