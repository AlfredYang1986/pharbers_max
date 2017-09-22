package com.pharbers.aqll.alMSA.alMaxLoadFile

import com.pharbers.aqll.alCalc.almain.alShareData
import com.pharbers.aqll.common.alFileHandler.alFilesOpt.alFileOpt


object alMaxLoadSegment {
	def apply(p: String): alMaxLoadSegment =new alMaxLoadSegment(p)
}

class alMaxLoadSegment(p: String) extends alLoadFile{
	
	override lazy val path: String = p
	
	lazy val reload_segment: List[(String, (Double, Double, Double))] = {
		var segmentLst: List[(String, (Double, Double, Double))] = Nil
		val dir = alFileOpt(path)
		if (!dir.isExists)
			dir.createDir
		
		val source = alFileOpt(path + "/" + "segmentData")
		if (source.isExists) {
			source.enumDataWithFunc { line =>
				val s = alShareData.txtSegmentGroupData(line)
				segmentLst = segmentLst :+ (s.segement, (s.sales, s.units, s.calc))
			}
		}
		segmentLst
	}
}
