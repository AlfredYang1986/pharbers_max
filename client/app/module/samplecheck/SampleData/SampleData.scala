package module.samplecheck.SampleData

import com.pharbers.aqll.common.alFileHandler.alFilesOpt.alFileOpt

trait SampleData {
	
	val csv2SampleCheckData : String => List[String Map String] = { path =>
		
		alFileOpt(s"/Users/qianpeng/Desktop/$path").requestDataFromFile(x => x).map { x =>
			val s =  x.toString.split(31.toChar)
			Map("phaId" -> s(5), "hospitalName" -> s(1), "date" -> s(2), "productMini" -> s(3), "market" -> s(7), "units" -> s(9), "sales" -> s(10))
		}
	}
	
}