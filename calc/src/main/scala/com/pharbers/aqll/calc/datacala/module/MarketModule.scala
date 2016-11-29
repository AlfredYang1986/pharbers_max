package com.pharbers.aqll.calc.datacala.module

import com.pharbers.aqll.calc.datacala.common._
import com.pharbers.aqll.calc.datacala.algorithm.maxUnionAlgorithm
import com.pharbers.aqll.calc.excel.helpFunc.StringOption
import com.pharbers.aqll.calc.datacala.module.MarketMessage.msg_IntegratedData
import com.pharbers.aqll.calc.maxmessages.MaxMessageTrait
import com.pharbers.aqll.calc.datacala.algorithm.maxCalcUnionAlgorithm
import com.pharbers.aqll.calc.datacala.module.MarketMessage.msg_MaxData
import com.pharbers.aqll.calc.datacala.algorithm.backWriterSumVolumFunction

object MarketModule {
    def dispatchMessage(dataMsg: MaxMessageTrait): Option[DataIOTrait] = dataMsg match{
        case msg_IntegratedData(data) => integratedData(data)
        case msg_MaxData(data) => maxData(data)
        case _ => ???
    }
    
    def integratedData(data : BaseArgs): Option[DataIOTrait] = data.data match {
        case (AdminMarkeDataArgs(market),AdminHospMatchDataArgs(hospmatchdata),UserMarketDataArgs(listCpaMarket)) => {
            lazy val elem3 = market filter(_.getDatasource.equals("CPA")) sortBy(_.getMinMarket)
            lazy val elem2 = hospmatchdata sortBy (_.getHospNum)
            lazy val elem1 = listCpaMarket sortBy (x => (x.getHospNum,x.getMarketname))
            lazy val hospNum = elem1.map (_.getHospNum).distinct
            lazy val integratedData = maxUnionAlgorithm.market(elem1,elem2,elem3)((e1,e2) => StringOption.takeStringSpace(e1.getMarketname).equals(e2.getMinMarket))
            Some(IntegratedDataArgs(integratedData))
        }
        case (AdminMarkeDataArgs(market),AdminHospMatchDataArgs(hospmatchdata),UserPhaMarketDataArgs(listPhaMarket)) => {
            lazy val elem3 = market filter(_.getDatasource.equals("PharmaTrust")) sortBy(_.getMinMarket)
            lazy val elem2 = hospmatchdata sortBy (_.getHospNum)
            lazy val elem1 = listPhaMarket sortBy (x => (x.getHospNum,x.getMarketname))
            lazy val hospNum = elem1.map (_.getHospNum).distinct
            lazy val integratedData = maxUnionAlgorithm.market(elem1,elem2,elem3)((e1,e2) => StringOption.takeStringSpace(e1.getMarketname).equals(e2.getMinMarket))
            Some(IntegratedDataArgs(integratedData))
        }
        case _ => ???
    } 
    
    def maxData(data: BaseMaxDataArgs): Option[DataIOTrait] = data.data match {
        case (AdminHospDataBaseArgs(hsopdata),IntegratedDataArgs(union)) => {
            lazy val data_max_new = backWriterSumVolumFunction(maxCalcUnionAlgorithm(union,hsopdata).sortBy(x => x.sortConditions1), union.sortBy(y => y.sortConditions1))(x => x.sortConditions1)(y => y.sortConditions1)
            Some(ModelRunDataArgs(data_max_new))
        }
    }
}