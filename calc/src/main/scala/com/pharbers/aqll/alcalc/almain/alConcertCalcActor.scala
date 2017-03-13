package com.pharbers.aqll.alcalc.almain

import akka.actor.{Actor, ActorLogging, Props}
import com.pharbers.aqll.alcalc.aldata.alStorage
import com.pharbers.aqll.alcalc.alfilehandler.altext.FileOpt
import com.pharbers.aqll.alcalc.aljobs.alJob.worker_core_calc_jobs
import com.pharbers.aqll.alcalc.aljobs.aljobtrigger.alJobTrigger._
import com.pharbers.aqll.alcalc.alprecess.alprecessdefines.alPrecessDefines.presist_data
import com.pharbers.aqll.alcalc.alstages.alStage
import com.pharbers.aqll.calc.common.DefaultData
import com.pharbers.aqll.calc.excel.IntegratedData.IntegratedData
import com.pharbers.aqll.calc.excel.model.westMedicineIncome

import scala.concurrent.stm.{Ref, atomic}

/**
  * Created by Alfred on 13/03/2017.
  */
object alConcertCalcActor {
    def props : Props = Props[alConcertCalcActor]
}

class alConcertCalcActor extends Actor
                            with ActorLogging {

    val index = Ref(-1)

    override def receive = {
        case concert_adjust() => sender() ! concert_adjust_result(-1)
        case concert_adjust_result(i) => atomic { implicit tnx =>
            index() = i
        }
        case concert_calc(p) => {
            val cj = worker_core_calc_jobs(Map(worker_core_calc_jobs.max_uuid -> p.uuid, worker_core_calc_jobs.calc_uuid -> p.subs(index.single.get).uuid))
            cj.result

            val concert = cj.cur.get.storages.head.asInstanceOf[alStorage]

            concert.data.foreach (x => max_precess(x.asInstanceOf[IntegratedData], p.subs(index.single.get).uuid))

            println(s"concert index ${index.single.get} end")

//            val m = alStorage.groupBy (x =>
//                (x.asInstanceOf[IntegratedData].getYearAndmonth, x.asInstanceOf[IntegratedData].getMinimumUnitCh)
//            )(concert)

//            val g = alStorage(m.values.map (x => x.asInstanceOf[alStorage].data.head.toString).toList)
//            g.doCalc
//            val sg = alStage(g :: Nil)
//            val pp = presist_data(Some(p.subs(index.single.get).uuid), Some("group"))
//            pp.precess(sg)
//            sender() ! concert_group_result(p.subs(index.single.get).uuid)
        }
        case _ => ???
    }

    def max_precess(element2 : IntegratedData, sub_uuid : String) = {
        val tmp =
        alShareData.hospdata map { element =>
            new westMedicineIncome(element.getCompany, element2.getYearAndmonth, 0, 0, element2.getMinimumUnit,
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
        }

        val path = s"config/calc/$sub_uuid"
        val dir = FileOpt(path)
        if (!dir.isExist)
            dir.createDir

        val file = FileOpt(path + "/" + "data")
        if (!file.isExist)
            file.createFile

        // 回填值

        file.appendData2File(tmp)
    }
}