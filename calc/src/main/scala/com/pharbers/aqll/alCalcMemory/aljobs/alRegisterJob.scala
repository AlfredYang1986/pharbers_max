package com.pharbers.aqll.alCalcMemory.aljobs

import akka.actor.ActorSelection
import com.pharbers.aqll.alCalcEnergy.alAkkaMonitoring.alRegisterCommond
import com.pharbers.aqll.alCalcMemory.alprecess.alRegister

/**
  * Created by qianpeng on 2017/6/7.
  */
trait alRegisterJob {
	var process: List[alRegister] = Nil
	
	var cur: Option[List[alRegisterCommond]] = None
	
	def excute(): Unit = if (!process.isEmpty) nextRun()
	
	def nextRun(): Unit = {
		if (!process.isEmpty) {
			val p = process.head
			process = process.tail
			cur match {
				case None => throw new Exception("job needs current stage")
				case Some(lst) => {
					lst foreach { x =>
						p.precess(x)
					}
				}
				case _ => ???
			}
			nextRun()
		}
	}
}
