package com.pharbers.aqll.alStart.alEntry

import java.util.UUID
import akka.actor.ActorSystem
import akka.cluster.Cluster
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxMaster.{pushCalcYMJob, pushGeneratePanelJob, pushSplitPanelJob}
import com.pharbers.aqll.alStart.alHttpFunc.alPanelItem
import com.pharbers.driver.redis.phRedisDriver
import com.typesafe.config.ConfigFactory

object alActorTest extends App {
	val config = ConfigFactory.load("split-test")
	val system : ActorSystem = ActorSystem("calc", config)

	val uid = "test-uid"
	val company = "fea9f203d4f593a96f0d6faa91ba24ba"
	val cpa_file_local = "1705 CPA.xlsx"
	val gycx_file_local = "1705 GYC.xlsx"
	val csv_panel = "0c6d8a01-4603-4c81-967f-28446c46d34c"
	val excel_panel = "../../201706/CPA_GYCX_panel_201706INF.xlsx"

	if (system.settings.config.getStringList("akka.cluster.roles").contains("splittest")){
		Cluster(system).registerOnMemberUp {
//			val a = system.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/driver-actor")
			val a = system.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
//			val redisDriver = phRedisDriver().commonDriver

//			// 通过用户登录产生的token获取company_name
//			val company = redisDriver.hget(s"bearer${uid}", "user_id").get
//			redisDriver.pipeline{ pipe =>
//				pipe.hset(s"calc:${uid}", "company", s"${company}")
//				pipe.hset(s"calc:${uid}", "rid", s"${rid}")
//				pipe.hset(s"calc:${uid}", "cpa", s"${cpa_file_local}")
//				pipe.hset(s"calc:${uid}", "gycx", s"${gycx_file_local}")
//			}

			//test calc ym
			if(false){
				a ! pushCalcYMJob(alPanelItem("fea9f203d4f593a96f0d6faa91ba24ba", "uid", cpa_file_local, gycx_file_local))
				println("======================================== test calc ym")
			}

			//test generter panel
//			if(true) {
//				println("================================== test generter panel")
//				a ! pushGeneratePanelJob(alPanelItem(company, uid, cpa_file_local, gycx_file_local, List("201705")))
//			} else {
//				val rid = UUID.randomUUID().toString
//				redisDriver.hset(uid, "rid", rid)
//				redisDriver.hset(uid, "company", company)
//				redisDriver.sadd(rid,csv_panel)
//			}

			//test split -> group -> calc -> bson
			if(true){
				1 to 2 foreach { x =>
					a ! pushSplitPanelJob("uid")
					println("===================== test split -> group -> calc -> bson")
				}
//				println("===================== test split -> group -> calc -> bson")
				//		a ! push_filter_job("/mnt/config/FileBase/201705/CPA_GYCX_panel_201705INF.xlsx", cp)
				//		a ! push_filter_job("/mnt/config/FileBase/201705/CPA_GYCX_panel_201705Specialty.xlsx", cp)
				//		a ! push_filter_job("/mnt/config/FileBase/201705/CPA_GYCX_panel_201705Urology.xlsx", cp)
				//		a ! push_filter_job("/mnt/config/FileBase/201705/CPA_GYCX_panel_201705AI_R.xlsx", cp)
				//		a ! push_filter_job("/mnt/config/FileBase/201705/CPA_GYCX_panel_201705AI_S.xlsx", cp)
				//		a ! push_filter_job("/mnt/config/FileBase/201705/CPA_GYCX_panel_201705AI_D.xlsx", cp)
				//		a ! push_filter_job("/mnt/config/FileBase/201705/CPA_GYCX_panel_201705AI_W.xlsx", cp)
				//		a ! push_filter_job("/mnt/config/FileBase/201705/CPA_GYCX_panel_201705CNS_R.xlsx", cp)
				//		a ! push_filter_job("/mnt/config/FileBase/201705/CPA_GYCX_panel_201705CNS_Z.xlsx", cp)
				//		a ! push_filter_job("/mnt/config/FileBase/201705/CPA_GYCX_panel_201705DVP.xlsx", cp)
				//		a ! push_filter_job("/mnt/config/FileBase/201705/CPA_GYCX_panel_201705ELIQUIS.xlsx", cp)
				//		a ! push_filter_job("/mnt/config/FileBase/201705/CPA_GYCX_panel_201705HTN.xlsx", cp)
				//		a ! push_filter_job("/mnt/config/FileBase/201705/CPA_GYCX_panel_201705HTN2.xlsx", cp)
				//		a ! push_filter_job("/mnt/config/FileBase/201705/CPA_GYCX_panel_201705LD.xlsx", cp)
				//		a ! push_filter_job("/mnt/config/FileBase/201705/CPA_GYCX_panel_201705ONC.xlsx", cp)
				//		a ! push_filter_job("/mnt/config/FileBase/201705/CPA_GYCX_panel_201705PAIN.xlsx", cp)
				//		a ! push_filter_job("/mnt/config/FileBase/201705/CPA_GYCX_panel_201705PAIN_C.xlsx", cp)
				//		a ! push_filter_job("/mnt/config/FileBase/201705/CPA_GYCX_panel_201705ZYVOX.xlsx", cp)
//				a ! pushSplitPanelJob("uid")
			}
		}
	}
}
