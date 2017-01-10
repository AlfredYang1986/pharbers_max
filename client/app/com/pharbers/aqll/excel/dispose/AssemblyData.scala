package com.pharbers.aqll.excel.dispose

import com.pharbers.aqll.excel.model._
import com.pharbers.aqll.util._

object queryCoresData{
    def hospitalInfo(data: List[AdminHospitalDataBase]): List[Map[String, AnyRef]] = {
      data.map( x=> Map(
        "HospInfo_Id" -> MD5.md5(x.getHospName+x.getPhaid),
        "Hosp_Name" -> x.getHospName,
        "Pha_Code" -> x.getPhaid,
        "Hosp_level" -> x.getHospLevel,
        "Specialty" -> x.getSpecialty1,
        "If_County" -> x.getIfCounty,
        "Region" -> x.getRegion,
        "Province_Name" -> x.getProvince,
        "City_Name" -> x.getPrefecture,
        "City_Tier" -> x.getCityTier,
        "Specialty_Classification" -> x.getSpecialty2))
    }
    def minimumProductInfo(data: List[AdminProduct]): Iterable[Map[String, AnyRef]] = {
      data.groupBy(z => z.getMinimumUnitCh).map(x =>
        Map(
          "MiniProdInfo_Id" -> MD5.md5(x._1),
          "MiniProd_Name_Ch" -> x._1,
          "MiniProd_Name_En" -> x._2.groupBy(x => x.getMinimumUnitEn).map(x => x._1).head,
          "Manufacturer_Ch" -> x._2.groupBy(x => x.getManufacturerCh).map(x => x._1).head,
          "Manufacturer_En" -> x._2.groupBy(x => x.getManufacturerEn).map(x => x._1).head,
          "Drug_Ch" -> x._2.groupBy(x => x.getGeneralnameCh).map(x => x._1).head,
          "Drug_En" -> x._2.groupBy(x => x.getGeneralnameEn).map(x => x._1).head,
          "Products_Ch" -> x._2.groupBy(x => x.getTradenameCh).map(x => x._1).head,
          "Products_En" -> x._2.groupBy(x => x.getTradenameEn).map(x => x._1).head,
          "DosageForm_Ch" -> x._2.groupBy(x => x.getDosageformsCh).map(x => x._1).head,
          "DosageForm_En" -> x._2.groupBy(x => x.getDosageformsEn).map(x => x._1).head,
          "DrugSpecification_Ch" -> x._2.groupBy(x => x.getDrugspecificationsCh).map(x => x._1).head,
          "DrugSpecification_En" -> x._2.groupBy(x => x.getDrugspecificationsEn).map(x => x._1).head,
          "Package_Quantity_Ch" -> x._2.groupBy(x => x.getNumberPackagingCh).map(x => x._1).head,
          "Package_Quantity_En" -> x._2.groupBy(x => x.getNumberPackagingEn).map(x => x._1).head,
          "sku_Ch" -> x._2.groupBy(x => x.getSkuCh).map(x => x._1).head,
          "sku_En" -> x._2.groupBy(x => x.getSkuEn).map(x => x._1).head
        )
      )
    }
}

