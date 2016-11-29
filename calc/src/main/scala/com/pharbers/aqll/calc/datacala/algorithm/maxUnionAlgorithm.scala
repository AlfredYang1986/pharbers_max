package com.pharbers.aqll.calc.datacala.algorithm

import com.pharbers.aqll.calc.excel.common.commonProductObjectTrait
import com.pharbers.aqll.calc.excel.Manage.AdminHospitalMatchingData
import com.pharbers.aqll.calc.excel.Manage.AdminProduct
import com.pharbers.aqll.calc.excel.Manage.AdminMarket
import com.pharbers.aqll.calc.excel.common.commonMarketObjectTrait
import com.pharbers.aqll.calc.excel.model.integratedData


//import excel.model.CPA.CpaProduct

object maxUnionAlgorithm {
    def product(elem1 : Stream[commonProductObjectTrait], elem2 : Stream[AdminProduct], elem3 :Stream[AdminHospitalMatchingData])(func : (commonProductObjectTrait, AdminProduct) => Boolean) = {
        lazy val hospNum = elem1.map(_.getHospNum.asInstanceOf[Number].longValue()).distinct
        (elem1.filter(x => hospNum.contains(x.getHospNum)).map { x =>
            val phaProduct = x
            val product_opt = elem2.find(x_opt => func(x, x_opt))
            val hospMatch_opt = elem3.find(_.getHospNum == x.getHospNum)
            (hospMatch_opt, product_opt) match {
                case (Some(hospMatch), Some(product)) =>
                    Some(new integratedData(phaProduct.getUploadYear, phaProduct.getUploadMonth, hospMatch.getDatasource, hospMatch.getHospNum, phaProduct.getSumValue, phaProduct.getVolumeUnit, product.getMinimumUnit, product.getMinimumUnitCh, product.getMinimumUnitEn, product.getManufacturerCh, product.getManufacturerEn, product.getGeneralnameCh, product.getGeneralnameEn, product.getTradenameCh, product.getTradenameEn, product.getDosageformsCh, product.getDosageformsEn, product.getDrugspecificationsCh, product.getDrugspecificationsEn, product.getNumberPackagingCh, product.getNumberPackagingEn, product.getSkuCh, product.getSkuEn, product.getMarket1Ch, product.getMarket1En, hospMatch.getHospNameCh, hospMatch.getHospNameEn, hospMatch.getHospLevelCh, hospMatch.getHospLevelEn, hospMatch.getAreaCh, hospMatch.getAreaEn, hospMatch.getProvinceCh, hospMatch.getProvinceEn, hospMatch.getCityCh, hospMatch.getCityEn))
                case _ => None
            }
        }).filter(x => x != None).map(x => x match {
            case Some(x) => x
            case None    => ???
        })
    }
    
    def market(elem1: Stream[commonMarketObjectTrait],elem2: Stream[AdminHospitalMatchingData],elem3: Stream[AdminMarket])(func: (commonMarketObjectTrait, AdminMarket) => Boolean) = {
        lazy val hospNum = elem1.map (_.getHospNum).distinct
        (elem1.filter(x => hospNum.contains(x.getHospNum)).map{x =>
            val cpaMarket = x
            val market_opt = elem3.find(x_opt => func(x, x_opt))
            val hospMatch_opt = elem2.find(_.getHospNum == x.getHospNum)
            (market_opt,hospMatch_opt) match{
                case (Some(market),Some(hospMatch)) =>
                    Some(new integratedData(cpaMarket.getUploadYear, cpaMarket.getUploadMonth, hospMatch.getDatasource, hospMatch.getHospNum, cpaMarket.getSumValue, cpaMarket.getVolumeUnit, market.getMinMarket, market.getMinMarketCh, market.getMinMarketEn, null, null, null, null, null, null, null, null, null, null, null, null, null, null, market.getMarket1Ch, market.getMarket1En, hospMatch.getHospNameCh, hospMatch.getHospNameEn, hospMatch.getHospLevelCh, hospMatch.getHospLevelEn, hospMatch.getAreaCh, hospMatch.getAreaEn, hospMatch.getProvinceCh, hospMatch.getProvinceEn, hospMatch.getCityCh, hospMatch.getCityEn))
                case _ =>None
            }
        }).filter(x => x != None).map(x => x match{
            case Some(y) => y
            case _ => ???
        })
    }
}