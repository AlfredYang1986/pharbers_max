//
//	when(calc_maxing) {
//		case Event(push_calc_job_2(mp, cp), pr) => {
//			acts ! push_calc_job_2(mp, cp)
//			stay()
//		}
//
//		case Event(calc_data_end(r, mp), pr) => {
//			pr.uuid = mp.uuid
//			println(s"mp.finalValue=${mp.finalValue}")
//			println(s"mp.finalUnit=${mp.finalUnit}")
//			val sub_uuids = mp.subs.map { p =>
//				p.isCalc = true
//				p.uuid
//			}
//			acts ! push_restore_job(s"${pr.company}${mp.uuid}", sub_uuids)
//
//			val msg = Map(
//				"type" -> "progress_calc",
//				"txt" -> "准备还原数据库",
//				"progress" -> "98"
//			)
//			alWebSocket(pr.uid).post(msg)
//			mp.isCalc = true
//			goto(restore_maxing) using pr
//		}
//	}
//
//	when(restore_maxing) {
//		case Event(restore_bson_end(result, sub_uuid), pr) => {
//			val msg = Map(
//				"type" -> "progress_calc",
//				"txt" -> "还原数据库结束",
//				"progress" -> "100"
//			)
//			alWebSocket(pr.uid).post(msg)
//			acts ! calc_slave_status()
//			test_num = test_num + 1
//			endDate("test" + test_num, s1)
//			shutCameo()
//			goto(alDriverJobIdle) using new alCalcParmary("", "")
//		}
//	}
//
//	when(calc_done) {
//		case Event(max_calc_done(mp), _) =>
//			val company = mp.get("company").getOrElse("")
//			val uuid = mp.get("uuid").getOrElse("")
//			val imuname = mp.get("imuname").getOrElse("")
//			val uid = mp.get("uid").getOrElse("")
//
//			val msg1 = Map(
//				"type" -> "progress_calc_result",
//				"txt" -> "正在转储为永久数据中",
//				"progress" -> "1"
//			)
//			alWebSocket(uid).post(msg1)
//			alWeightSum().apply(company, s"$company$uuid")
//
//			val msg2 = Map(
//				"type" -> "progress_calc_result",
//				"txt" -> "成功",
//				"progress" -> "100"
//			)
//			alWebSocket(uid).post(msg2)
//			println("转储为永久数据成功")
//			dbc.getCollection(s"$company$uuid").drop()
//			shutCameo
//			goto(alDriverJobIdle) using new alCalcParmary("", "")
//	}