package com.pharbers.aqll.alMSA.alMaxSlaves

import java.io.File
import java.nio.charset.StandardCharsets
import java.util.UUID

import scala.concurrent.stm.{Ref, atomic}
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.pharbers.alCalcMemory.aldata.alStorage
import com.pharbers.alCalcMemory.alstages.alStage
import com.pharbers.aqll.alCalaHelp.alMaxDefines.{alCalcParmary, alMaxProperty, endDate, startDate}
import com.pharbers.aqll.alCalc.almain.{alSegmentGroup, alShareData}
import com.pharbers.aqll.alCalc.almodel.scala.westMedicineIncome
import com.pharbers.aqll.alCalc.almodel.java.IntegratedData
import com.pharbers.aqll.alCalcMemory.aljobs.alJob.{common_jobs, worker_core_calc_jobs}
import com.pharbers.aqll.alCalcMemory.alprecess.alprecessdefines.alPrecessDefines._
import com.pharbers.alCalcMemory.alprecess.alsplitstrategy.server_info
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcData._
import com.pharbers.aqll.common.alDate.java.DateUtil
import com.pharbers.aqll.common.alEncryption.alEncryptionOpt
import com.pharbers.aqll.common.alFileHandler.alFilesOpt.alFileOpt
import com.pharbers.aqll.common.alFileHandler.fileConfig.{calc, memorySplitFile, sync}
import com.pharbers.baseModules.PharbersInjectModule
import com.pharbers.bson.writer.{bsonFlushMemory, phBsonWriter}
import com.pharbers.memory.pages.fop.dir.dirPageStorage
import com.pharbers.memory.pages.{dirFlushMemory, pageMemory, pageMemory2}

/**
  * Created by alfredyang on 13/07/2017.
  */

object alCalcDataImpl {
    val sumSender = Ref(List[ActorRef]())
    val sumSegment = Ref(List[(String, (Double, Double, Double))]())
    def props = Props[alCalcDataImpl]
}

class alCalcDataImpl extends Actor with ActorLogging with PharbersInjectModule {
    import alCalcDataImpl._

    override val id: String = "restore-path"
    override val configPath: String = "pharbers_config/restore_path.xml"
    override val md = "bson-path" :: Nil

    val bson_path = config.mc.find(p => p._1 == "bson-path").get._2.toString

    var unit: Double = 0.0
	var value: Double = 0.0
	val maxSum: scala.collection.mutable.Map[String, (Double, Double, Double)] = scala.collection.mutable.Map.empty

    var tmp : alMaxProperty = null
    override def receive: Receive = {
        case calc_data_hand() => sender ! calc_data_hand()
        case calc_data_start_impl(p, c) => {
            log.info("&& T7 START &&")
            val t7 = startDate()
            println("&& T7 && alCalcDataImpl.calc_data_start_impl")
            tmp = p
            val cj = worker_core_calc_jobs(Map(worker_core_calc_jobs.max_uuid -> p.uuid, worker_core_calc_jobs.calc_uuid -> p.subs.head.uuid))
            cj.result

            val concert = cj.cur.get.storages.head.asInstanceOf[alStorage]


            val recall = resignIntegratedData(p.parent)(concert)
            concert.data.zipWithIndex.foreach { x =>

                max_precess(x._1.asInstanceOf[IntegratedData],
                    p.subs.head.uuid,
                    Some(s"${x._2}/${concert.data.length}"))(recall)(c)
            }
            log.info(s"concert uuid ${p.subs.head.uuid} end")
            val s = (maxSum.toList.groupBy(_._1) map { x =>
                (x._1, (x._2.map(z => z._2._1).sum, x._2.map(z => z._2._2).sum, x._2.map(z => z._2._3).sum))
            }).toList
            atomic { implicit txn =>
                
                sumSender() = sumSender.single.get :+ sender()
                sumSegment() = sumSegment.single.get ++ s
                if(sumSender.single.get.size == server_info.cpu) {
                    val uid = UUID.randomUUID().toString
                    val path = s"${memorySplitFile}${calc}$uid"
                    val temp = sumSegment.single.get map { x => alSegmentGroup(x._1, x._2._1, x._2._2, x._2._3)}
                    val dir = alFileOpt(path)
                    if(!dir.isExists) dir.createDir
                    val file = alFileOpt(path + "/" + "segmentData")
                    if (!file.isExists) file.createFile
                    file.appendData2File(temp)
                    sumSender.single.get.foreach(_ ! calc_data_sum2(path))
                    sumSender() = Nil
                    sumSegment() = Nil
                }
            }
            endDate("&& T7 && ", t7)
            log.info("&& T7 END &&")
            // TODO : 超出传输界限
//            sender ! calc_data_sum(s)
        }

        case calc_data_average2(avg_path) => {
            import scala.math.BigDecimal
            log.info("&& T10 START &&")
            val t10 = startDate()
            println("&& T10 && alCalcDataImpl.calc_data_average2")
            val sub_uuid = tmp.subs.head.uuid

            val path = s"${memorySplitFile}${calc}$sub_uuid"

            val dir = alFileOpt(path)
            if (!dir.isExists)
                dir.createDir
//            val source = alFileOpt(path + "/" + "data")
            val source = new File(path)
//            val bw_path = s"config/dumpdb/Max_Cores/${sub_uuid}.bson"
//            val bw = phBsonWriter(bw_path)
            val bfm = bsonFlushMemory(bson_path)
            if (source.exists && source.isDirectory) {

                val avg = alFileOpt(avg_path).requestDataFromFile(x => x).map { x =>
                                val line_tmp = x.toString.split(",")
                                (line_tmp(0), line_tmp(1).toDouble, line_tmp(2).toDouble)
                            }
                            
                val dr = dirPageStorage(path)
                dr.readAllData { line =>

//                val page = pageMemory2(path + "/" + "data")
//                val totalPage = page.pageCount.toInt
//                (0 until totalPage) foreach { i =>
//                    page.pageData(i).foreach { line =>

                        val mrd = alShareData.txt2WestMedicineIncome2(line)
                        val seed = mrd.segment + mrd.minimumUnitCh + mrd.yearAndmonth.toString
                        if (mrd.ifPanelAll == "1") {
                            mrd.set_finalResultsValue(mrd.sumValue)
                            mrd.set_finalResultsUnit(mrd.volumeUnit)
                        } else {

                            avg.find(p => p._1 == seed.hashCode.toString).map { x =>
                                mrd.set_finalResultsValue(BigDecimal((x._2 * mrd.selectvariablecalculation.get._2 * mrd.factor.toDouble).toString).toDouble)
                                mrd.set_finalResultsUnit(BigDecimal((x._3 * mrd.selectvariablecalculation.get._2 * mrd.factor.toDouble).toString).toDouble)
                            }.getOrElse(Unit)
                        }

                        unit = BigDecimal((unit + mrd.finalResultsUnit).toString).toDouble
                        value = BigDecimal((value + mrd.finalResultsValue).toString).toDouble
                        val map_tmp = westMedicineIncome2map(mrd)

//                        bw.writeBsonFile2(bw.map2bson(map_tmp))
                         bfm.appendObject(bfm.map2bson(map_tmp))
                    }
//                }

                bfm.close
//                bw.flush
//                bw.close
//                page.closeStorage

                log.info(s"calc done at ${sub_uuid}")

            }else {
//                log.info(s"Error! source=${source} not exist!")
                sender() ! calc_data_end(true, tmp)
            }
            sender() ! calc_data_result(value, unit)
            sender() ! calc_data_end(true, tmp)
            endDate("&& T10 && ", t10)
            log.info("&& T10 END &&")
        }

        case msg : Any => log.info(s"Error msg=[${msg}] was not delivered.in actor=${self}")
    }

    def max_precess(element2: IntegratedData, sub_uuid: String, longPath: Option[String] = None)(recall: List[IntegratedData])(c: alCalcParmary) = {
        if (!longPath.isEmpty) {
            log.info(s"concert calc in $longPath")
            val universe = alEncryptionOpt.md5(c.company + c.year + c.market)
            val data_tmp =
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

            val path = s"${memorySplitFile}${calc}$sub_uuid"

            val dir = alFileOpt(path)
            if (!dir.isExists)
                dir.createDir

//            val file = alFileOpt(path + "/" + "data")
//            if (!file.isExists)
//                file.createFile
//
//            file.appendData2File2(data_tmp)

            val d = dirFlushMemory(path)
            data_tmp.foreach (l => d.appendLine(l.toString))
            d.close
        }
    }

    def resignIntegratedData(parend_uuid: String)(group: alStorage): List[IntegratedData] = {
        val recall = common_jobs()
        val path = s"${memorySplitFile}${sync}$parend_uuid"
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
        recall.cur.get.storages.head.asInstanceOf[alStorage].data.asInstanceOf[List[IntegratedData]]
    }

    def backfireData(mrd: westMedicineIncome)(inte_lst: List[IntegratedData]): westMedicineIncome = {
        /**
          * 根据年月 + 最小产品单位 + phaid 找到Panle文件中的sales 与 unit
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
                        (x._2._1 + mrd.sumValue, x._2._2 + mrd.volumeUnit, x._2._3 + mrd.selectvariablecalculation.get._2)
                }.getOrElse((mrd.sumValue, mrd.volumeUnit, mrd.selectvariablecalculation.get._2))
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
