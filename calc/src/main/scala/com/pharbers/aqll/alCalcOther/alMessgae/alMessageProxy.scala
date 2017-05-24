package com.pharbers.aqll.alCalcOther.alMessgae

import com.pharbers.aqll.alCalcOther.alLog.alLoggerMsgTrait
import com.pharbers.aqll.common.alErrorCode.alErrorCode
import io.swagger.client.ApiException
import io.swagger.client.model.{Msg, MsgContent, UserName}

/**
  * Created by qianpeng on 2017/5/24.
  */
class alMessageProxy extends alMessage with alLoggerMsgTrait {
	
	override def sendMsg(content: String, toUser: String, ext: Map[String, String] = Map.empty): Boolean = {
		try {
			val userName = new UserName()
			userName.add(toUser)
			invokeEasemobAPI(new Msg().from("project").target(userName).targetType("users").msg(new MsgContent().`type`(MsgContent.TypeEnum.TXT).msg(content)).ext(ext))
			true
		} catch {
			case e: ApiException =>
				e.getCode match {
					case 401 => logger.error(alErrorCode.errorToJson("error client secre").toString())
					case 500 => logger.error(alErrorCode.errorToJson("error server").toString())
					case _ => logger.error(alErrorCode.errorToJson("unknow error").toString())
				}
				false
		}
	}
}
