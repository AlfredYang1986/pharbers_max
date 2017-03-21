package com.pharbers.aqll.alcalc.almain

import java.io.{FileWriter, PrintWriter}

import akka.actor.{Actor, ActorLogging, Props}
import com.pharbers.aqll.alcalc.aldata.alStorage
import com.pharbers.aqll.alcalc.alfilehandler.altext.FileOpt
import com.pharbers.aqll.alcalc.aljobs.alJob.{common_jobs, worker_core_calc_jobs}
import com.pharbers.aqll.alcalc.aljobs.aljobtrigger.alJobTrigger.{concert_calc_avg, _}
import com.pharbers.aqll.alcalc.alprecess.alprecessdefines.alPrecessDefines._
import com.pharbers.aqll.alcalc.alstages.alStage
import com.pharbers.aqll.calc.common.DefaultData
import com.pharbers.aqll.calc.excel.IntegratedData.IntegratedData
import com.pharbers.aqll.calc.excel.model.{ModelRunFactory, modelRunData, westMedicineIncome2}
import com.pharbers.aqll.calc.util.GetProperties

import scala.concurrent.stm.{Ref, atomic}

/**
  * Created by Alfred on 13/03/2017.
  */
object alConcertCalcActor {
    def props : Props = Props[alConcertCalcActor]
}

class alConcertCalcActor extends Actor
                            with ActorLogging {

    var unit = 0.0.toDouble
    var value = 0.0.toDouble
    val index = Ref(-1)
    val maxSum: scala.collection.mutable.Map[String, (Double, Double, Double)] = scala.collection.mutable.Map.empty

    override def receive = {
        case concert_adjust() => sender() ! concert_adjust_result(-1)
        case concert_adjust_result(i) => atomic { implicit tnx =>
            index() = i
        }
        case concert_calc(p) => {
            val cj = worker_core_calc_jobs(Map(worker_core_calc_jobs.max_uuid -> p.uuid, worker_core_calc_jobs.calc_uuid -> p.subs(index.single.get).uuid))
            cj.result

            val concert = cj.cur.get.storages.head.asInstanceOf[alStorage]

            val recall = resignIntegratedData(p.parent)(concert)
            concert.data.zipWithIndex.foreach (x =>
                max_precess(x._1.asInstanceOf[IntegratedData],
                            p.subs(index.single.get).uuid,
                            Some(x._2 + "/" + concert.data.length))
                        (recall)
            )

            println(s"concert index ${index.single.get} end")

            sender() ! concert_calc_sum_result(p.subs(index.single.get).uuid, maxSum.toList)
        }
        case concert_calc_avg(p, avg) => {
            println(s"avg at ${index.single.get} is $avg")

            val sub_uuid = p.subs(index.single.get).uuid
//            val path = s"config/calc/$sub_uuid"
            val path = s"${GetProperties.memorySplitFile}${GetProperties.calc}$sub_uuid"
            val dir = FileOpt(path)
            if (!dir.isExist)
                dir.createDir

            val source = FileOpt(path + "/" + "data")
            if (source.isExist) {
                val target = (path + "/" + "result")

                val writer = new FileWriter(target, true);
                source.enumDataWithFunc { line =>
                    val mrd = alShareData.txt2WestMedicineIncome2(line)

                    avg.find (p => p._1 == mrd.segment).map { x =>
                        if (mrd.ifPanelAll.equals("1")) {
                            mrd.set_finalResultsValue(mrd.sumValue)
                            mrd.set_finalResultsUnit(mrd.volumeUnit)
                            // mrd.finalResultsValue = mrd.sumValue
                            // mrd.finalResultsUnit = mrd.volumeUnit
                        }else{
                            mrd.set_finalResultsValue(x._2 * mrd.selectvariablecalculation.get._2 * mrd.factor.toDouble)
                            mrd.set_finalResultsUnit(x._3 * mrd.selectvariablecalculation.get._2 * mrd.factor.toDouble)
                            // mrd.finalResultsValue = x._2 * mrd.selectvariablecalculation.get._2 * mrd.factor.toDouble
                            // mrd.finalResultsUnit = x._3 * mrd.selectvariablecalculation.get._2 * mrd.factor.toDouble
                        }

                    }.getOrElse (Unit)

                    if (mrd.finalResultsValue > 0) {
                        unit = unit + mrd.finalResultsUnit
                        value = value + mrd.finalResultsValue
                    }
                    writer.write(mrd.toString + "\n")
                }
                writer.flush()
                writer.close()

                println(s"calc done at ${index.single.get}")
            }

            sender() ! concert_calc_result(sub_uuid, value, unit)
        }
        case _ => ???
    }

    def max_precess(element2 : IntegratedData, sub_uuid : String, log : Option[String] = None)(recall : List[IntegratedData]) = {
        if (!log.isEmpty)
            println(s"concert index ${index.single.get} calc in $log")
            val tmp =
                alShareData.hospdata map { element =>
                    // val mrd = new westMedicineIncome(element.getCompany, element2.getYearAndmonth, 0, 0, element2.getMinimumUnit,
                    val mrd = westMedicineIncome2(element.getCompany, element2.getYearAndmonth, 0.0, 0.0, element2.getMinimumUnit,
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

//        val path = s"config/calc/$sub_uuid"
        val path = s"${GetProperties.memorySplitFile}${GetProperties.calc}$sub_uuid"
        val dir = FileOpt(path)
        if (!dir.isExist)
            dir.createDir

        val file = FileOpt(path + "/" + "data")
        if (!file.isExist)
            file.createFile

        file.appendData2File(tmp)
    }

    def resignIntegratedData(parend_uuid : String)(group : alStorage) : List[IntegratedData] = {
        val recall = common_jobs()
//        val path = s"config/sync/$parend_uuid"
        val path = s"${GetProperties.memorySplitFile}${GetProperties.sync}$parend_uuid"
        recall.cur = Some(alStage(FileOpt(path).lstFiles))
        recall.process = restore_data() :: do_calc() :: do_union() ::
                            do_map (alShareData.txt2IntegratedData(_)) :: do_filter { iter =>
                                val t = iter.asInstanceOf[IntegratedData]
                                group.data.exists { g =>
                                    val x = g.asInstanceOf[IntegratedData]
                                    (x.getYearAndmonth == t.getYearAndmonth) && (x.getMinimumUnitCh == t.getMinimumUnitCh)
                                }
                            } :: do_calc() :: Nil
        recall.result
        println(s"current ${index.single.get} recall data length ${recall.cur.get.length}")
        recall.cur.get.storages.head.asInstanceOf[alStorage].data.asInstanceOf[List[IntegratedData]]
    }

    def backfireData(mrd : westMedicineIncome2)(inte_lst : List[IntegratedData]) : westMedicineIncome2 = {
        var t = mrd
        val tmp = inte_lst.find(iter => mrd.yearAndmonth == iter.getYearAndmonth
                                    && mrd.minimumUnitCh == iter.getMinimumUnitCh
                                    && mrd.phaid == iter.getPhaid)

        tmp match {
            case Some(x) => {
                // mrd.sumValue = x.getSumValue
                // mrd.volumeUnit = x.getVolumeUnit
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