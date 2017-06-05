package com.pharbers.aqll.alCalcMemory.aljobs

import com.pharbers.aqll.alCalcMemory.alprecess.alFilePrecess
import com.pharbers.aqll.common.alCmd.alShellCmdExce


/**
  * Created by qianpeng on 2017/3/17.
  */
trait alPkgJob {
	var process : List[alFilePrecess] = Nil

	var cur : Option[List[alShellCmdExce]] = None

	def excute(): Unit = {
		if (!process.isEmpty) nextRun()
	}

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
