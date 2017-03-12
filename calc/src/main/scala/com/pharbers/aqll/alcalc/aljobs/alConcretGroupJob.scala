package com.pharbers.aqll.alcalc.aljobs

import com.pharbers.aqll.alcalc.alprecess.alprecessdefines.alPrecessDefines._
import com.pharbers.aqll.calc.excel.IntegratedData.IntegratedData
import com.pharbers.aqll.alcalc.alprecess.alsplitstrategy.alSplitStrategy.core_split
import com.pharbers.aqll.alcalc.alstages.alStage
import com.pharbers.aqll.calc.excel.IntegratedData.IntegratedData
import com.pharbers.aqll.calc.util.StringOption

/**
  * Created by Alfred on 11/03/2017.
  */
class alConcretGroupJob(u : String, val parent : String) extends alJob {
    override val uuid: String = u
    val ps = presist_data(Some(uuid))

    def init(args : Map[String, Any]) = {
        val restore_path = """config/sync/""" + parent + "/" + uuid
        cur = Some(alStage(restore_path))
        process = restore_data() :: do_map { txt => 
            val t = new IntegratedData()
            val x = txt.asInstanceOf[String].split(",")
			t.setHospNum(StringOption.takeStringSpace(x(0)).toInt)
			t.setHospName(StringOption.takeStringSpace(x(1)))
			t.setYearAndmonth(StringOption.takeStringSpace(x(2)).toInt)
			t.setMinimumUnit(StringOption.takeStringSpace(x(3)))
			t.setMinimumUnitCh(StringOption.takeStringSpace(x(4)))
			t.setMinimumUnitEn(StringOption.takeStringSpace(x(5)))
			t.setPhaid(StringOption.takeStringSpace(x(6)))
			t.setStrength(StringOption.takeStringSpace(x(7)))
			t.setMarket1Ch(StringOption.takeStringSpace(x(8)))
			t.setMarket1En(StringOption.takeStringSpace(x(9)))
			t.setSumValue(StringOption.takeStringSpace(x(10)).toDouble)
			t.setVolumeUnit(StringOption.takeStringSpace(x(11)).toDouble)
			t
        } :: do_calc() :: Nil
    }
    override def result : Option[Any] =  {
        super.result
        ps.result
    }
}