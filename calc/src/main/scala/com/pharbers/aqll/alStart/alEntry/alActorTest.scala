package com.pharbers.aqll.alStart.alEntry

import java.util.UUID
import akka.cluster.Cluster
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import com.pharbers.driver.redis.phRedisDriver
import com.pharbers.aqll.alStart.alHttpFunc.alPanelItem
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxMaster.{pushCalcYMJob, pushGeneratePanelJob, pushSplitPanel}

object alActorTest extends App {
	val config = ConfigFactory.load("split-test")
	val system : ActorSystem = ActorSystem("calc", config)

	val uid = "uid"
	val company = "fea9f203d4f593a96f0d6faa91ba24ba"
	val cpa_file_local = "1705 CPA.xlsx"
	val gycx_file_local = "1705 GYC.xlsx"

	if (system.settings.config.getStringList("akka.cluster.roles").contains("splittest")){
		Cluster(system).registerOnMemberUp {
			val a = system.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
			val redisDriver = phRedisDriver().commonDriver

			// 通过用户登录产生的token获取company_name
			redisDriver.pipeline{ pipe =>
				pipe.hset(s"calc:${uid}", "company", s"${company}")
			}

			//test calc ym
			if(false){
				println("======================================== test calc ym")
				a ! pushCalcYMJob(alPanelItem(company, uid, cpa_file_local, gycx_file_local))
			}

			//test generter panel
			if(false) {
				println("================================== test generter panel")
				a ! pushGeneratePanelJob(alPanelItem(company, uid, cpa_file_local, gycx_file_local, List("201705")))
				Thread.sleep(300000)
			} else {
				println("================================== write panel to redis")
				val rid = UUID.randomUUID().toString
				redisDriver.hset(uid, "rid", rid)
//				redisDriver.sadd(rid, "CPA_GYCX_panel_201705INF.xlsx")
//				redisDriver.sadd(rid, "CPA_GYCX_panel_201705Specialty.xlsx")
//				redisDriver.sadd(rid, "CPA_GYCX_panel_201705Urology.xlsx")
//				redisDriver.sadd(rid, "CPA_GYCX_panel_201705AI_R.xlsx")
//				redisDriver.sadd(rid, "CPA_GYCX_panel_201705AI_S.xlsx")
//				redisDriver.sadd(rid, "CPA_GYCX_panel_201705AI_D.xlsx")
//				redisDriver.sadd(rid, "CPA_GYCX_panel_201705AI_W.xlsx")
//				redisDriver.sadd(rid, "CPA_GYCX_panel_201705CNS_R.xlsx")
//				redisDriver.sadd(rid, "CPA_GYCX_panel_201705CNS_Z.xlsx")
//				redisDriver.sadd(rid, "CPA_GYCX_panel_201705DVP.xlsx")
//				redisDriver.sadd(rid, "CPA_GYCX_panel_201705ELIQUIS.xlsx")
//				redisDriver.sadd(rid, "CPA_GYCX_panel_201705HTN.xlsx")
//				redisDriver.sadd(rid, "CPA_GYCX_panel_201705HTN2.xlsx")
//				redisDriver.sadd(rid, "CPA_GYCX_panel_201705LD.xlsx")
//				redisDriver.sadd(rid, "CPA_GYCX_panel_201705ONC.xlsx")
//				redisDriver.sadd(rid, "CPA_GYCX_panel_201705PAIN.xlsx")
//				redisDriver.sadd(rid, "CPA_GYCX_panel_201705PAIN_C.xlsx")
//				redisDriver.sadd(rid, "CPA_GYCX_panel_201705ZYVOX.xlsx")
				Thread.sleep(3000)
			}

			//test split -> group -> calc -> bson
			if (true) {
				1 to 1 foreach { x =>
					println("===================== test split -> group -> calc -> bson")
					a ! pushSplitPanel(uid)
				}
			}
		}
	}
}
