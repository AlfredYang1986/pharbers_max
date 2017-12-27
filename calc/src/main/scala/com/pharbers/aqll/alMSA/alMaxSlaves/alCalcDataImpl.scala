package com.pharbers.aqll.alMSA.alMaxSlaves

import java.io.File
import java.util.UUID
import scala.math.BigDecimal
import akka.actor.{Actor, ActorLogging, Props}
import com.pharbers.driver.redis.phRedisDriver
import com.pharbers.bson.writer.bsonFlushMemory
import com.pharbers.memory.pages.dirFlushMemory
import com.pharbers.alCalcMemory.aldata.alStorage
import com.pharbers.alCalcMemory.alstages.alStage
import com.pharbers.aqll.alCalcHelp.alLog.alTempLog
import com.pharbers.baseModules.PharbersInjectModule
import com.pharbers.aqll.common.alDate.java.DateUtil
import com.pharbers.memory.pages.fop.dir.dirPageStorage
import com.pharbers.aqll.common.alEncryption.alEncryptionOpt
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.calcMsg._
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import com.pharbers.aqll.alCalcHelp.alFinalDataProcess.alBsonPath
import com.pharbers.aqll.alCalcHelp.alModel.java.IntegratedData
import com.pharbers.aqll.alCalcHelp.alModel.scala.westMedicineIncome
import com.pharbers.aqll.common.alFileHandler.alFilesOpt.alFileOpt
import com.pharbers.aqll.alCalcHelp.{alSegmentGroup, alShareData}
import com.pharbers.aqll.alCalcMemory.alprecess.alprecessdefines.alPrecessDefines._
import com.pharbers.aqll.common.alFileHandler.fileConfig.{calc, memorySplitFile, sync}
import com.pharbers.aqll.alCalcMemory.aljobs.alJobs.{common_jobs, worker_core_calc_jobs}

/**
  * Created by alfredyang on 13/07/2017.
  *     Modify by clock on 2017.12.20
  */
object alCalcDataImpl {
    def props = Props[alCalcDataImpl]
}

class alCalcDataImpl extends Actor with ActorLogging{
    var unit: Double = 0.0
    var value: Double = 0.0
    var bson_path = alBsonPath().bson_file_path
    val maxSum: scala.collection.mutable.Map[String, (Double, Double, Double)] = scala.collection.mutable.Map.empty

    override def receive: Receive = {
        case calc_data_hand() => sender ! calc_data_hand()

        case calc_data_start_impl3(sub_item, items) => {
            val cj = worker_core_calc_jobs(
                Map(worker_core_calc_jobs.max_uuid -> sub_item.parent,
                    worker_core_calc_jobs.calc_uuid -> sub_item.tid)
            )
            cj.result
            val concert = cj.cur.get.storages.head.asInstanceOf[alStorage]
            val recall = resignIntegratedData(sub_item.parent)(concert)

            concert.data.zipWithIndex.foreach { x =>
                max_precess(
                    x._1.asInstanceOf[IntegratedData],
                    sub_item.tid,
                    Some(s"${x._2}/${concert.data.length}")
                )(recall)(sub_item.uid)
            }

            val f = (m1: Map[String, Any], m2: Map[String, Any]) => m1.map(x => x._1 -> (x._2.toString.toDouble + m2(x._1).toString.toDouble))

            val rdSet = phRedisDriver().phSetDriver
            maxSum.foreach { x =>
                //TODO key 没有唯一性
                rdSet.sadd("segment", alSegmentGroup(x._1, x._2._1, x._2._2, x._2._3).map, f)
            }

            val agent = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
            agent ! sumCalcJob(items, sender)
        }

        case calc_data_average_one(avg_path, bsonpath) => sender ! calc_data_average_one(avg_path, bsonpath)

        case calc_data_average_post(item, panel, avg_path, bsonpath) => {
            val sub_uuid = item.tid
            val path = s"$memorySplitFile$calc$sub_uuid"
            val dir = alFileOpt(path)
            if (!dir.isExists)
                dir.createDir
            bson_path = bson_path + bsonpath
            val dir2 = alFileOpt(bson_path)
            if (!dir2.isExists)
                dir2.createDir

            val source = new File(path)
            val bfm = bsonFlushMemory(bson_path)
            if (source.exists && source.isDirectory) {
                val avg = alFileOpt(avg_path).requestDataFromFile(x => x).map { x =>
                    val line_tmp = x.toString.split(",")
                    (line_tmp(0), line_tmp(1).toDouble, line_tmp(2).toDouble)
                }
                val dr = dirPageStorage(path)
                dr.readAllData { line =>
                    val mrd = alShareData.txt2WestMedicineIncome2(line)
                    val seed = mrd.segment + mrd.minimumUnitCh + mrd.yearAndmonth.toString
                    if (mrd.ifPanelAll == "1") {
                        mrd.set_finalResultsValue(mrd.sumValue)
                        mrd.set_finalResultsUnit(mrd.volumeUnit)
                    } else {
                        avg.find(p => p._1 == seed.hashCode.toString).foreach { x =>
                            mrd.set_finalResultsValue(BigDecimal((x._2 * mrd.selectvariablecalculation().get._2 * mrd.factor).toString).toDouble)
                            mrd.set_finalResultsUnit(BigDecimal((x._3 * mrd.selectvariablecalculation().get._2 * mrd.factor).toString).toDouble)
                        }
                    }
                    value = BigDecimal((value + mrd.finalResultsValue).toString).toDouble
                    unit = BigDecimal((unit + mrd.finalResultsUnit).toString).toDouble
                    val map_tmp = westMedicineIncome2map(mrd)
                    bfm.appendObject(bfm.map2bson(map_tmp))
                }
                bfm.close
                alTempLog(s"C5 Calc write $sub_uuid bson => Success")
                log.info(s"calc done at $sub_uuid")
            }

            val agent = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
            agent ! calcDataResult(true, item.uid, panel, value, unit)
        }

        case msg: Any => alTempLog(s"Warning! Message not delivered. alCalcDataImpl.received_msg=$msg")
    }

    private def resignIntegratedData(parent_uuid: String)(group: alStorage): List[IntegratedData] = {
        val recall = common_jobs()
        val path = s"$memorySplitFile$sync$parent_uuid"
        recall.cur = Some(alStage(alFileOpt(path).exHideListFile))
        recall.process = restore_data() :: do_calc() :: do_union() ::
                do_map(alShareData.txt2IntegratedData(_)) :: do_filter { iter =>
                    val t = iter.asInstanceOf[IntegratedData]
                    group.data.exists { g => //true
                        val x = g.asInstanceOf[IntegratedData]
                        (x.getYearAndmonth == t.getYearAndmonth) && (x.getMinimumUnitCh == t.getMinimumUnitCh)
                    }
                } :: do_calc() :: Nil
        recall.result
        log.info(s"current recall data length ${recall.cur.get.length}")
        alTempLog(s"C3. current recall data length ${recall.cur.get.length}")
        recall.cur.get.storages.head.asInstanceOf[alStorage].data.asInstanceOf[List[IntegratedData]]
    }

    private def max_precess(element: IntegratedData, sub_uuid: String, longPath: Option[String] = None)
                           (recall: List[IntegratedData])(uid: String) = {
        if (!longPath.isEmpty) {
            val company = phRedisDriver().commonDriver.hget(uid, "company").get

            val data_tmp = alShareData.hospdata("universe", company) map { el =>
                val mrd = westMedicineIncome(el.getCompany, element.getYearAndmonth, 0.0, 0.0, element.getMinimumUnit,
                    element.getMinimumUnitCh, element.getMinimumUnitEn, element.getMarket1Ch,
                    element.getMarket1En, el.getSegment, el.getFactor, el.getIfPanelAll,
                    el.getIfPanelTouse, el.getHospId, el.getHospName, el.getPhaid,
                    el.getIfCounty, el.getHospLevel, el.getRegion, el.getProvince,
                    el.getPrefecture, el.getCityTier, el.getSpecialty1, el.getSpecialty2,
                    el.getReSpecialty, el.getSpecialty3, el.getWestMedicineIncome, el.getDoctorNum,
                    el.getBedNum, el.getGeneralBedNum, el.getMedicineBedNum, el.getSurgeryBedNum,
                    el.getOphthalmologyBedNum, el.getYearDiagnosisNum, el.getClinicNum, el.getMedicineNum,
                    el.getSurgeryNum, el.getHospitalizedNum, el.getHospitalizedOpsNum, el.getIncome,
                    el.getClinicIncome, el.getClimicCureIncome, el.getHospitalizedIncome,
                    el.getHospitalizedBeiIncome, el.getHospitalizedCireIncom, el.getHospitalizedOpsIncome,
                    el.getDrugIncome, el.getClimicDrugIncome, el.getClimicWestenIncome,
                    el.getHospitalizedDrugIncome, el.getHospitalizedWestenIncome, 0.0, 0.0)
                backfireData(mrd)(recall)
            }

            val path = s"$memorySplitFile$calc$sub_uuid"
            val dir = alFileOpt(path)
            if (!dir.isExists)
                dir.createDir

            val d = dirFlushMemory(path)
            data_tmp.foreach (l => d.appendLine(l.toString))
            d.close
        }
    }

    private def backfireData(mrd: westMedicineIncome)(inte_lst: List[IntegratedData]): westMedicineIncome = {
        /**
          * 根据年月 + 最小产品单位 + PHA_ID 找到Panle文件中的sales 与 unit
          * 回填到被放大的数据中
          */
        val tmp2 = inte_lst.filter(iter => mrd.yearAndmonth == iter.getYearAndmonth
            && mrd.minimumUnitCh == iter.getMinimumUnitCh
            && mrd.phaid == iter.getPhaid)

        tmp2 match {
            case Nil => Unit
            case list => {
                val sumValue = list.map(x => x.getSumValue.toDouble).sum
                val sumUnits = list.map(x => x.getVolumeUnit.toDouble).sum
                mrd.set_sumValue(sumValue)
                mrd.set_volumeUnit(sumUnits)
            }
        }

        /**
          * 进行segment的分组动作，并求和
          */
        if (mrd.ifPanelTouse == "1") {
            val seed = mrd.segment + mrd.minimumUnitCh + mrd.yearAndmonth.toString
             maxSum += seed.hashCode.toString ->
                 maxSum.find(p => p._1 == seed.hashCode.toString).map { x =>
                        (x._2._1 + mrd.sumValue, x._2._2 + mrd.volumeUnit, x._2._3 + mrd.selectvariablecalculation().get._2)
                }.getOrElse((mrd.sumValue, mrd.volumeUnit, mrd.selectvariablecalculation().get._2))
        }
        mrd.copy()
    }

    def westMedicineIncome2map(mrd: westMedicineIncome) : Map[String, Any] = {
        Map("ID" -> alEncryptionOpt.md5(UUID.randomUUID().toString),
            "Provice" -> mrd.getV("province").toString,
            "City" -> mrd.getV("prefecture").toString,
            "Panel_ID" -> mrd.phaid,
            "Market" -> mrd.getV("market1Ch").toString,
            "Product" ->  mrd.minimumUnitCh,
            "f_units" -> mrd.finalResultsUnit,
            "f_sales" -> mrd.finalResultsValue,
            "Date" -> DateUtil.getDateLong(mrd.yearAndmonth.toString),
            "prov_Index" -> alEncryptionOpt.md5(mrd.getV("province").toString + mrd.getV("market1Ch").toString + mrd.minimumUnitCh + DateUtil.getDateLong(mrd.yearAndmonth.toString)),
            "city_Index" -> alEncryptionOpt.md5(mrd.getV("province").toString + mrd.getV("prefecture").toString + mrd.getV("market1Ch").toString + mrd.minimumUnitCh + DateUtil.getDateLong(mrd.yearAndmonth.toString)),
            "hosp_Index" -> alEncryptionOpt.md5(mrd.getV("province").toString +
                mrd.getV("prefecture").toString +
                mrd.phaid +
                mrd.getV("market1Ch").toString +
                mrd.minimumUnitCh +
                DateUtil.getDateLong(mrd.yearAndmonth.toString)))
    }
}
