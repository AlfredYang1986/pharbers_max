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

case class AdminHospDataBaseArgs(val data: Stream[AdminHospitalDataBase]) extends CommonArg {
    type data_type = Stream[AdminHospitalDataBase]
}

case class AdminHospMatchDataArgs(val data: Stream[AdminHospitalMatchingData]) extends CommonArg {
    type data_type = Stream[AdminHospitalMatchingData]
}

case class AdminMarkeDataArgs(val data: Stream[AdminMarket]) extends CommonArg {
    type data_type = Stream[AdminMarket]
}

case class AdminProductDataArgs(val data: Stream[AdminProduct]) extends CommonArg {
    type data_type = Stream[AdminProduct]
}

/*************************************产品********************************************/
case class UserProductDataArgs(val data: Stream[CpaProduct]) extends CommonArg {
    type data_type = Stream[CpaProduct]
}

case class UserPhaProductDataArgs(val data: Stream[PharmaTrustPorduct]) extends CommonArg {
    type data_type = Stream[PharmaTrustPorduct]
}

/************************************市场********************************************/
case class UserMarketDataArgs(val data: Stream[CpaMarket]) extends CommonArg {
    type data_type = Stream[CpaMarket]
}

case class UserPhaMarketDataArgs(val data: Stream[PharmaTrustMarket]) extends CommonArg {
    type data_type = Stream[PharmaTrustMarket]
}

case class IntegratedDataArgs(val data: Stream[integratedData]) extends CommonArg {
    type data_type = Stream[integratedData]
}

case class ModelRunDataArgs(val data: List[modelRunData]) extends CommonArg {
    type data_type = List[modelRunData]
}
