package controllers.immessage

import javax.inject.Inject

import akka.actor.ActorSystem
import play.api.libs.json.Json.toJson
import play.api.mvc.Action
import controllers.common.requestArgsQuery
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.token.AuthTokenTrait
import module.immessage.ImRoomMessage.MsgCreateImRooms

class ImRoomController @Inject ()(as_inject : ActorSystem, dbt : dbInstanceManager, att : AuthTokenTrait){
	implicit val as = as_inject
	
	def createChatRooms = Action (request => requestArgsQuery().requestArgsV2(request) { jv =>
		import com.pharbers.bmpattern.ResultMessage.common_result
		import com.pharbers.bmpattern.LogMessage.common_log
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("createChatRooms"))), jv) :: MsgCreateImRooms(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
	})
}
