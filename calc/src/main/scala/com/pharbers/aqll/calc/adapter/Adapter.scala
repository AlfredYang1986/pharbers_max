package com.pharbers.aqll.calc.adapter

import com.pharbers.aqll.calc.datacala.common._
import com.pharbers.aqll.calc.datacala.module.MaxMessage.msg_IntegratedData
import com.pharbers.aqll.calc.excel.model.integratedData
import com.pharbers.aqll.calc.datacala.module.MaxModule
import com.pharbers.aqll.calc.excel.model.modelRunData
import com.pharbers.aqll.calc.datacala.algorithm.maxCalcUnionAlgorithm

import com.pharbers.aqll.calc.util.StringOption
import com.pharbers.aqll.calc.datacala.algorithm.maxUnionAlgorithm
import com.pharbers.aqll.calc.excel.Manage.AdminMarket
import com.pharbers.aqll.calc.excel.CPA.CpaMarket
import com.pharbers.aqll.calc.excel.CPA.CpaProduct
import com.pharbers.aqll.calc.excel.PharmaTrust.PharmaTrustMarket
import com.pharbers.aqll.calc.excel.PharmaTrust.PharmaTrustPorduct
import com.pharbers.aqll.calc.excel.Manage.AdminHospitalMatchingData
import com.pharbers.aqll.calc.excel.common.commonMarketObjectTrait
import com.pharbers.aqll.calc.excel.Manage.AdminProduct
import com.pharbers.aqll.calc.excel.common.commonProductObjectTrait
import scala.collection.mutable.ArrayBuffer

trait Adapter {
    def splitdata(data: BaseArgs): Stream[integratedData] = Stream.Empty
    
    def integrateddata(data : BaseArgs): Option[DataIOTrait] = None
    
    def maxdata(data: BaseMaxDataArgs): Option[DataIOTrait] = None
}

trait ELementAdapter {
    
    def hospmatchdatafun(data: Stream[AdminHospitalMatchingData]): Stream[AdminHospitalMatchingData] = {
        data sortBy (_.getHospNum)
    }
    
    def marketdatasource(market: Stream[AdminMarket], str: String): Stream[AdminMarket] = {
        market filter(_.getDatasource.equals(str)) sortBy(_.getMinMarket)
    }
    
    def productdatasource(product: Stream[AdminProduct], str: String): Stream[AdminProduct] = {
        product filter (_.getDatasource.equals(str)) sortBy (_.getMinimumUnitCh)
    }
    
    def elemmarket(usermarket: Stream[commonMarketObjectTrait], str: String): Stream[integratedData] = Stream.Empty
    
    def elemproduct(userproduct: Stream[commonProductObjectTrait], str: String): Stream[integratedData] = Stream.Empty
}

abstract class DataType extends ELementAdapter {
    def market: Stream[AdminMarket]
    def product: Stream[AdminProduct]
}

sealed class AdapterSub(data: CommonArg, hospmatchdata: Stream[AdminHospitalMatchingData]) extends DataType {
    def market: Stream[AdminMarket] = {
        data match {
            case AdminMarkeDataArgs(m) => m
        }
    }
    def product: Stream[AdminProduct] = {
        data match {
            case AdminProductDataArgs(p) => p
        }
    }
    
    override def elemmarket(usermarket: Stream[commonMarketObjectTrait], str: String): Stream[integratedData] = {
        lazy val integratedData = maxUnionAlgorithm.market(usermarket,hospmatchdatafun(hospmatchdata),marketdatasource(market,str))((e1,e2) => StringOption.takeStringSpace(e1.getMarketname).equals(e2.getMinMarket))
        integratedData.toStream
    }
    
    override def elemproduct(userproduct: Stream[commonProductObjectTrait], str: String): Stream[integratedData] = {
         lazy val integratedData = maxUnionAlgorithm.product(userproduct, productdatasource(product, str), hospmatchdatafun(hospmatchdata))((e1, e2) => StringOption.takeStringSpace(e2.getMinimumUnit).equals(e1.commonObjectCondition))
        integratedData.toStream
    }
}



class MaxUnionAdapter extends Adapter {
    
    override def integrateddata(data: BaseArgs): Option[DataIOTrait] = {
        data.data match {
            case (AdminMarkeDataArgs(market),AdminHospMatchDataArgs(hospmatchdata),UserMarketDataArgs(listCpaMarket)) => {
                val integratedData =  new AdapterSub(new AdminMarkeDataArgs(market), hospmatchdata).elemmarket(listCpaMarket sortBy (x => (x.getHospNum,x.getMarketname)), "CPA")
                Some(IntegratedDataArgs(integratedData))
            }
            case (AdminMarkeDataArgs(market),AdminHospMatchDataArgs(hospmatchdata),UserPhaMarketDataArgs(listPhaMarket)) => {
                val integratedData =  new AdapterSub(new AdminMarkeDataArgs(market), hospmatchdata).elemmarket(listPhaMarket sortBy (x => (x.getHospNum,x.getMarketname)), "PharmaTrust")
                Some(IntegratedDataArgs(integratedData))
            }
            case (AdminProductDataArgs(product),AdminHospMatchDataArgs(hospmatchdata),UserProductDataArgs(listCpaProduct)) => {
                val integratedData =  new AdapterSub(new AdminProductDataArgs(product), hospmatchdata).elemproduct(listCpaProduct sortBy (x => (x.getHospNum, x.commonObjectCondition)), "CPA")
                Some(IntegratedDataArgs(integratedData))
            }
            case (AdminProductDataArgs(product),AdminHospMatchDataArgs(hospmatchdata),UserPhaProductDataArgs(listPhaProduct)) => {
                val integratedData =  new AdapterSub(new AdminProductDataArgs(product), hospmatchdata).elemproduct(listPhaProduct sortBy (x => (x.getHospNum, x.commonObjectCondition)), "PharmaTrust")
                Some(IntegratedDataArgs(integratedData))
            } 
            case _ => ???
        }
    }
    
    override def maxdata(data: BaseMaxDataArgs): Option[DataIOTrait] = {
            data.data match {
                case (AdminHospDataBaseArgs(hsopdata), IntegratedDataArgs(union)) => {
                    lazy val data_max_new = new maxCalcUnionAlgorithm()(union, hsopdata)
                    Some(ModelRunDataArgs(data_max_new))
                }
                case _ => ???
        }
    }
}


class SplitAdapter extends Adapter {
    
    override def splitdata(data: BaseArgs): Stream[integratedData] = {
        lazy val dataMsg = msg_IntegratedData(data)
        MaxModule.dispatchMessage(dataMsg) match {
            case Some(IntegratedDataArgs(igda)) => {
                igda
            }
            case _ => Stream.Empty
        }
    }
}
