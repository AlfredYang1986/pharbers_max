package com.pharbers.aqll.alCalcMemory.alprecess
import com.pharbers.aqll.alCalcEnergy.alAkkaMonitoring.alRegisterCommond

/**
  * Created by qianpeng on 2017/6/8.
  */
class alRegisterPrecess extends alRegister {
	override def precess(s: alRegisterCommond): Unit = {
		s.register()
	}
}
