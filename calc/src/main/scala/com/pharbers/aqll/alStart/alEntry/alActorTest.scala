package com.pharbers.aqll.alStart.alEntry

import java.util.UUID

import akka.actor.ActorSystem
import akka.cluster.Cluster
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxMaster.{pushCalcYMJob, pushSplitPanelJob, pushGeneratePanelJob}
import com.pharbers.aqll.alStart.alHttpFunc.alPanelItem
import com.pharbers.driver.redis.phRedisDriver
import com.typesafe.config.ConfigFactory

object alActorTest extends App {
	val config = ConfigFactory.load("split-test")
	val system : ActorSystem = ActorSystem("calc", config)
	val excel_panel = "config/FileBase/201706/CPA_GYCX_panel_201706INF.xlsx"
	val csv_panel = "config/FileBase/fea9f203d4f593a96f0d6faa91ba24ba/Output/0c6d8a01-4603-4c81-967f-28446c46d34c"
	val cpa_file_local = "1705 CPA.xlsx"
	val gycx_file_local = "1705 GYC.xlsx"

	if (system.settings.config.getStringList("akka.cluster.roles").contains("splittest")){
		Cluster(system).registerOnMemberUp {
			val a = system.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/driver-actor")

			val redisDriver = phRedisDriver().commonDriver
			/*val uid = "767d821137a00621022a1ce452b7a02d"    						// 之后前端发送 计算请求 只用传user_id
			val company = redisDriver.hget(s"bearer${uid}", "user_id").get			// 通过用户登录产生的token获取company_name
			val rid = UUID.randomUUID().toString									// 生成运行计算的running_id
			redisDriver.pipeline{ pipe =>
				pipe.hset(s"calc:${uid}", "company", s"${company}")
				pipe.hset(s"calc:${uid}", "rid", s"${rid}")
				pipe.hset(s"calc:${uid}", "cpa", s"${cpa_file_local}")
				pipe.hset(s"calc:${uid}", "gycx", s"${gycx_file_local}")
			}*/

			redisDriver.hset("uid", "company", "fea9f203d4f593a96f0d6faa91ba24ba")	//单独测试后端临时使用

			//test generter panel
			if(false){
				a ! pushGeneratePanelJob(alPanelItem("fea9f203d4f593a96f0d6faa91ba24ba", "uid", cpa_file_local, gycx_file_local, List("201705")))
				println("================================== test generter panel")
			}

			//test split -> group -> calc -> bson
			if(true){
				a ! pushSplitPanelJob("uid")
				println("===================== test split -> group -> calc -> bson")
			}

//			//test calc ym
			if(false){
				a ! pushCalcYMJob(alPanelItem("fea9f203d4f593a96f0d6faa91ba24ba", "uid", cpa_file_local, gycx_file_local))
				println("======================================== test calc ym")
			}
		}
	}
}
