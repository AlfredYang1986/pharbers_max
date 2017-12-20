package module.samplecheck.SampleData

import com.pharbers.aqll.common.alFileHandler.alFilesOpt.alFileOpt
import com.pharbers.driver.redis.phRedisDriver
import com.pharbers.panel.pfizer.panel_file_path
import com.pharbers.panel.util.excel.{phExcelData, phHandleExcel}
import com.pharbers.panel.util.excel.phHandleExcel.{filterFun, postFun}

import scala.collection.immutable.Map

trait SampleData extends panel_file_path{
	
	val csv2SampleCheckData : List[String] => List[String Map String] = { pathLst =>
		pathLst.flatMap{ p =>
			// TODO: 公司写死的
			alFileOpt(s"$base_path/fea9f203d4f593a96f0d6faa91ba24ba$output_local$p").requestDataFromFile(x => x).map { x =>
				val s =  x.toString.split(31.toChar)
				Map("phaId" -> s(5), "hospitalName" -> s(1), "date" -> s(2), "productMini" -> s(3), "market" -> s(7), "units" -> s(9), "sales" -> s(10))
			}
		}
	}
	
	val xlsx2SampleCheckData: String => List[String Map String] = { path =>
		implicit val postArg: String Map String => Option[String Map String] = postFun
		implicit val filterArg: String Map String => Boolean = filterFun
		// TODO: 公司写死的
		phHandleExcel().readExcel(phExcelData(s"$base_path/fea9f203d4f593a96f0d6faa91ba24ba${universe_inf_file.replace("##market##", path)}", 1))
	}
	
	def queryPanelWithRedis(uid: String): List[String] = {
		val rid = phRedisDriver().commonDriver.hget(uid, "rid").map(x=>x).getOrElse(throw new Exception("not found rid"))
		val panelLst = phRedisDriver().commonDriver.smembers(rid).map(x=>x.map(_.get)).getOrElse(throw new Exception("rid list is none"))
		panelLst.toList
	}
}