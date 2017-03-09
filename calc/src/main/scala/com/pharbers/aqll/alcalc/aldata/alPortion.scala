package com.pharbers.aqll.alcalc.aldata

/**
  * Created by BM on 09/03/2017.
  */

object alPortion {
    implicit val r2p : AnyRef => alPortion = x => alPortion(x)
}

case class alPortion(val d : AnyRef)
