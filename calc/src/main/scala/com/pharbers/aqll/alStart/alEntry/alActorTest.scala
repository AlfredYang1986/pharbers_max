package com.pharbers.aqll.alStart.alEntry

import java.util.UUID
import akka.cluster.Cluster
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import com.pharbers.driver.redis.phRedisDriver
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg._
import com.pharbers.aqll.alStart.alHttpFunc.alPanelItem
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP

object alActorTest extends App {
	val config = ConfigFactory.load("split-test")
	val system : ActorSystem = ActorSystem("calc", config)

	val company1 = "fea9f203d4f593a96f0d6faa91ba24ba"
	val cpa_file_local1 = "1705 CPA.xlsx"
	val gycx_file_local1 = "1705 GYC.xlsx"

	val company2 = "8ee0ca24796f9b7f284d931650edbd4b"
	val cpa_file_local2 = "to医药魔方 恩华CPA原始数据2015.1-2017.6.xlsx"

	if (system.settings.config.getStringList("akka.cluster.roles").contains("splittest")){
		Cluster(system).registerOnMemberUp {
			val agent = system.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
			val redisDriver = phRedisDriver().commonDriver

			(1 to 1) foreach { x =>
				val uid = "uid" + x
				redisDriver.hset(uid, "company", company1)

				//test calc ym
				if (false){
					println("======================================== test calc ym")
					agent ! startCalcYm(alPanelItem(company1, uid, cpa_file_local1, gycx_file_local1))
				}

				//test generter panel
				if (false) {
					println("================================== test generter panel")
					val ym = "201501" :: "201503" :: "201504" :: "201506" ::
							"201507" :: "201509" :: "201512" ::
							"201601" :: "201602" :: "201603" :: "201604" :: "201605" :: "201606" ::
							"201607" :: "201611" :: "201612" ::
							"201702" :: "201704" :: "201705" :: Nil
//					val ym = "201501" :: "201502" :: "201503" :: "201504" :: "201505" :: "201506" ::
//							"201507" :: "201508" :: "201509" :: "201510" :: "201511" :: "201512" ::
//							"201601" :: "201602" :: "201603" :: "201604" :: "201605" :: "201606" ::
//							"201607" :: "201608" :: "201609" :: "201610" :: "201611" :: "201612" ::
//							"201701" :: "201702" :: "201703" :: "201704" :: "201705" :: "201706" :: Nil
					agent ! startGeneratePanel(alPanelItem(company2, uid, cpa_file_local2, "", ym))
//					Thread.sleep(5 * 60 * 1000)
				} else if(true){
					println("================================== write panel to redis")
					val rid = UUID.randomUUID().toString
					redisDriver.hset(uid, "rid", rid)
					if(uid == "uid1"){
						redisDriver.sadd(rid, "CPA_GYCX_panel_201612INF.xlsx")
						redisDriver.hset("CPA_GYCX_panel_201612INF.xlsx", "mkt", "INF")
						redisDriver.hset("CPA_GYCX_panel_201612INF.xlsx", "ym", "201705")
//						redisDriver.sadd(rid, "CPA_GYCX_panel_201705Specialty.xlsx")
//						redisDriver.sadd(rid, "CPA_GYCX_panel_201705Urology.xlsx")
//						redisDriver.sadd(rid, "CPA_GYCX_panel_201705AI_S.xlsx")
//						redisDriver.sadd(rid, "CPA_GYCX_panel_201705AI_D.xlsx")
//						redisDriver.sadd(rid, "CPA_GYCX_panel_201705AI_W.xlsx")
//						redisDriver.sadd(rid, "CPA_GYCX_panel_201705CNS_R.xlsx")
//						redisDriver.sadd(rid, "CPA_GYCX_panel_201705CNS_Z.xlsx")
//						redisDriver.sadd(rid, "CPA_GYCX_panel_201705DVP.xlsx")
//						redisDriver.sadd(rid, "CPA_GYCX_panel_201705ELIQUIS.xlsx")
//						redisDriver.sadd(rid, "CPA_GYCX_panel_201705HTN.xlsx")
//						redisDriver.sadd(rid, "CPA_GYCX_panel_201705HTN2.xlsx")
//						redisDriver.sadd(rid, "CPA_GYCX_panel_201705LD.xlsx")
//						redisDriver.sadd(rid, "CPA_GYCX_panel_201705ONC.xlsx")
//						redisDriver.sadd(rid, "CPA_GYCX_panel_201705PAIN.xlsx")
//						redisDriver.sadd(rid, "CPA_GYCX_panel_201705PAIN_C.xlsx")
//						redisDriver.sadd(rid, "CPA_GYCX_panel_201705ZYVOX.xlsx")
//						redisDriver.sadd(rid, "CPA_GYCX_panel_201705AI_R.xlsx")
					} else if (uid == "uid2"){
						redisDriver.sadd(rid, "CPA_GYCX_panel_201706INF.xlsx")
						redisDriver.sadd(rid, "CPA_GYCX_panel_201706Specialty.xlsx")
						redisDriver.sadd(rid, "CPA_GYCX_panel_201706Urology.xlsx")
						redisDriver.sadd(rid, "CPA_GYCX_panel_201706AI_S.xlsx")
						redisDriver.sadd(rid, "CPA_GYCX_panel_201706AI_D.xlsx")
						redisDriver.sadd(rid, "CPA_GYCX_panel_201706AI_W.xlsx")
						redisDriver.sadd(rid, "CPA_GYCX_panel_201706CNS_R.xlsx")
						redisDriver.sadd(rid, "CPA_GYCX_panel_201706CNS_Z.xlsx")
						redisDriver.sadd(rid, "CPA_GYCX_panel_201706DVP.xlsx")
						redisDriver.sadd(rid, "CPA_GYCX_panel_201706ELIQUIS.xlsx")
						redisDriver.sadd(rid, "CPA_GYCX_panel_201706HTN.xlsx")
						redisDriver.sadd(rid, "CPA_GYCX_panel_201706HTN2.xlsx")
						redisDriver.sadd(rid, "CPA_GYCX_panel_201706LD.xlsx")
						redisDriver.sadd(rid, "CPA_GYCX_panel_201706ONC.xlsx")
						redisDriver.sadd(rid, "CPA_GYCX_panel_201706PAIN.xlsx")
						redisDriver.sadd(rid, "CPA_GYCX_panel_201706PAIN_C.xlsx")
						redisDriver.sadd(rid, "CPA_GYCX_panel_201706ZYVOX.xlsx")
						redisDriver.sadd(rid, "CPA_GYCX_panel_201706AI_R.xlsx")
					} else if (uid == "uid3"){
						redisDriver.sadd(rid, "CPA_GYCX_panel_201707INF.xlsx")
						redisDriver.sadd(rid, "CPA_GYCX_panel_201707Specialty.xlsx")
						redisDriver.sadd(rid, "CPA_GYCX_panel_201707Urology.xlsx")
						redisDriver.sadd(rid, "CPA_GYCX_panel_201707AI_S.xlsx")
						redisDriver.sadd(rid, "CPA_GYCX_panel_201707AI_D.xlsx")
						redisDriver.sadd(rid, "CPA_GYCX_panel_201707AI_W.xlsx")
						redisDriver.sadd(rid, "CPA_GYCX_panel_201707CNS_R.xlsx")
						redisDriver.sadd(rid, "CPA_GYCX_panel_201707CNS_Z.xlsx")
						redisDriver.sadd(rid, "CPA_GYCX_panel_201707DVP.xlsx")
						redisDriver.sadd(rid, "CPA_GYCX_panel_201707ELIQUIS.xlsx")
						redisDriver.sadd(rid, "CPA_GYCX_panel_201707HTN.xlsx")
						redisDriver.sadd(rid, "CPA_GYCX_panel_201707HTN2.xlsx")
						redisDriver.sadd(rid, "CPA_GYCX_panel_201707LD.xlsx")
						redisDriver.sadd(rid, "CPA_GYCX_panel_201707ONC.xlsx")
						redisDriver.sadd(rid, "CPA_GYCX_panel_201707PAIN.xlsx")
						redisDriver.sadd(rid, "CPA_GYCX_panel_201707PAIN_C.xlsx")
						redisDriver.sadd(rid, "CPA_GYCX_panel_201707ZYVOX.xlsx")
						redisDriver.sadd(rid, "CPA_GYCX_panel_201707AI_R.xlsx")
					}

					Thread.sleep(3000)
				}

				//test split -> group -> calc -> bson
				if(true){
					println("===================== test split -> group -> calc -> bson")
					agent ! startCalc(uid)
				}

				if(false){
					println("===================== test calc -> aggregation")
					agent ! startAggregationCalcData("47ee6f05c8994e9ddbe12c2971600766", Nil)
				}
			}
		}
	}
}
