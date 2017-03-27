package com.pharbers.aqll.alcalc.almodel

object westMedicineIncome {
	def apply(args: Any*) = {
		val tmp = new westMedicineIncome
		tmp.map = (properties zip args).toMap
		tmp
	}

	def fromString(args : String) = {
		val sub = args.split(31.toChar)
		val tmp = new westMedicineIncome
		sub.foreach { iter =>
			val lst = iter.split("=")
			if (lst.length == 2) {
				val a = lst.head
				val b = lst.tail.head
				tmp.map = tmp.map + (a -> b)
			}else {
				val a = lst.head
				val b = "无"
				tmp.map = tmp.map + (a -> b)
			}
		}
		tmp
	}

	val properties : List[String] = "company" :: "yearAndmonth" :: "sumValue" ::
			"volumeUnit" :: "minimumUnit" :: "minimumUnitCh" ::
			"minimumUnitEn" :: "market1Ch" :: "market1En" ::
			"segment" :: "factor" :: "ifPanelAll" ::
			"ifPanelTouse" :: "hospId" :: "hospName" ::
			"phaid" :: "ifCounty" :: "hospLevel" ::
			"region" :: "province" :: "prefecture" ::
			"cityTier" :: "specialty1" :: "specialty2" ::
			"reSpecialty" :: "specialty3" :: "westMedicineIncome" ::
			"doctorNum" :: "bedNum" :: "generalBedNum" :: "medicineBedNum" ::
			"surgeryBedNum" :: "ophthalmologyBedNum" :: "yearDiagnosisNum" ::
			"clinicNum" :: "medicineNum" :: "surgeryNum" ::
			"hospitalizedNum" :: "hospitalizedOpsNum" ::
			"income" :: "clinicIncome" :: "climicCureIncome" :: "hospitalizedIncome" ::
			"hospitalizedBeiIncome" :: "hospitalizedCireIncom" :: "hospitalizedOpsIncome" ::
			"drugIncome" :: "climicDrugIncome" :: "climicWestenIncome" ::
			"hospitalizedDrugIncome" :: "hospitalizedWestenIncome" :: "finalResultsValue" ::
			"finalResultsUnit" :: Nil
}

class westMedicineIncome {

	var map : Map[String, Any] = Map.empty

	def copy(): westMedicineIncome = {
		val m: westMedicineIncome = this
		val tmp = new westMedicineIncome
		tmp.map = m.map
		tmp
	}

	def selectvariablecalculation(): Option[(String, Double)] = {
		val t = map.get("westMedicineIncome").map { x => 
			if (!x.isInstanceOf[Double])
				x.toString.toDouble
			else
				x.asInstanceOf[Double]

		}.getOrElse(???)
		Some("西药收入", t)
	}

	override def toString: String = {

		val lst = westMedicineIncome.properties.map { x =>
			map.get(x).map(y => s"""$x=$y${31.toChar}""").getOrElse(s"""$x=${31.toChar}""")
		}
		val buf = new StringBuffer
		lst foreach ( x => buf.append(x))
		buf.toString
	}

	def finalResultsValue : Double = getV("finalResultsValue") match {
		case d: Double => d
		case s: String => s.toString.toDouble
		case _ => ???
	}
	def finalResultsUnit : Double = getV("finalResultsUnit") match {
		case d: Double => d
		case s: String => s.toString.toDouble
		case _ => ???
	}

	def segment : String = getV("segment").toString
	def ifPanelAll : String = getV("ifPanelAll").toString
	def ifPanelTouse : String = getV("ifPanelTouse").toString

	def yearAndmonth : Int = getV("yearAndmonth") match {
		case d: Int => d
		case s: String => s.toString.toInt
		case _ => ???
	}
	def minimumUnitCh : String = getV("minimumUnitCh").toString
	def phaid : String = getV("phaid").toString

	def sumValue : Double = getV("sumValue") match {
		case d: Double => d
		case s: String => s.toString.toDouble
		case _ => ???
	}
	def volumeUnit : Double = getV("volumeUnit") match {
		case d: Double => d
		case s: String => s.toDouble
		case _ => ???
	}
	def factor: Double = getV("factor") match {
		case d : Double => d
		case s : String => s.toString.toDouble
		case _ => ???
	}
	

	def set_sumValue(v : Double) = setV("sumValue", v)
	def set_volumeUnit(v : Double) = setV("volumeUnit", v)

	def set_finalResultsValue(v : Double) = setV("finalResultsValue", v)
	def set_finalResultsUnit(v : Double) = setV("finalResultsUnit", v)

	def setV(k : String, v : Double) = map = map + (k -> v)

	// def getV(index : Int) : Any = {
	// 	val k = properties(index)
	// 	tmp.get(k).map(x => x).getOrElse(???)
	// }

	def getV(k : String) : Any = {
		// println(map)
		map.get(k).map(x => x).getOrElse("无")
	}
}
