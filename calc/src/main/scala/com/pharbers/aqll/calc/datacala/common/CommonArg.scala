package com.pharbers.aqll.calc.datacala.common

import com.pharbers.aqll.calc.excel.Manage._
import com.pharbers.aqll.calc.excel.CPA._
import com.pharbers.aqll.calc.excel.PharmaTrust._
import com.pharbers.aqll.calc.excel.model._


trait DataIOTrait {
    type data_type
    val data : data_type
}

trait MaxArgsTrait extends DataIOTrait 

abstract class CommonArg extends MaxArgsTrait{
    type data_type <: AnyRef
}

case class BaseArgs(val data : (CommonArg, CommonArg, CommonArg)) extends MaxArgsTrait {
    type data_type = (CommonArg, CommonArg, CommonArg)
}

case class BaseMaxDataArgs(data: (CommonArg,CommonArg)) extends MaxArgsTrait {
    type data_type = (CommonArg,CommonArg)
}

case class BaseExcelUnionArgs(data: CommonArg) extends MaxArgsTrait {
    type data_type = (CommonArg)
}

case class AdminHospDataBaseArgs(val data: List[AdminHospitalDataBase]) extends CommonArg {
    type data_type = List[AdminHospitalDataBase]
}

case class AdminHospMatchDataArgs(val data: List[AdminHospitalMatchingData]) extends CommonArg {
    type data_type = List[AdminHospitalMatchingData]
}

case class AdminMarkeDataArgs(val data: List[AdminMarket]) extends CommonArg {
    type data_type = List[AdminMarket]
}

case class AdminProductDataArgs(val data: List[AdminProduct]) extends CommonArg {
    type data_type = List[AdminProduct]
}

/*************************************产品********************************************/
case class UserProductDataArgs(val data: List[CpaProduct]) extends CommonArg {
    type data_type = List[CpaProduct]
}

case class UserPhaProductDataArgs(val data: List[PharmaTrustPorduct]) extends CommonArg {
    type data_type = List[PharmaTrustPorduct]
}

/************************************市场********************************************/
case class UserMarketDataArgs(val data: List[CpaMarket]) extends CommonArg {
    type data_type = List[CpaMarket]
}

case class UserPhaMarketDataArgs(val data: List[PharmaTrustMarket]) extends CommonArg {
    type data_type = List[PharmaTrustMarket]
}

case class IntegratedDataArgs(val data: List[integratedData]) extends CommonArg {
    type data_type = List[integratedData]
}

case class ModelRunDataArgs(val data: List[modelRunData]) extends CommonArg {
    type data_type = List[modelRunData]
}
