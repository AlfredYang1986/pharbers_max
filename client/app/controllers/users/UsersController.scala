package controllers.users

import javax.inject.Inject

import akka.actor.ActorSystem
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.message.send.SendMessageTrait
import com.pharbers.token.AuthTokenTrait
import controllers.common.requestArgsQuery
import module.auth.AuthMessage.{MsgAuthTokenExpire, MsgAuthTokenParser, _}
import module.register.RegisterMessage.msg_first_push_user
import module.users.UserMessage._
import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, Controller}

class UsersController @Inject () (as_inject : ActorSystem, dbt : dbInstanceManager, att : AuthTokenTrait, msg: SendMessageTrait) extends Controller {
	implicit val as: ActorSystem = as_inject
	
	def user_push = Action(request => requestArgsQuery().requestArgsV2(request) {jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(msg_user_push(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "msg" -> msg))))
	})
	
	def user_delete = Action(request => requestArgsQuery().requestArgsV2(request) {jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(msg_user_delete(jv) ::  msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "msg" -> msg))))
	})
	
	def user_update = Action(request => requestArgsQuery().requestArgsV2(request) {jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(msg_user_update(jv) ::  msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "msg" -> msg))))
	})
	
	def user_query = Action(request => requestArgsQuery().requestArgsV2(request) {jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(msg_user_query(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "msg" -> msg))))
	})
	
	def user_query_info = Action(request => requestArgsQuery().requestArgsV2(request) {jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(msg_user_query_info(jv) ::  msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "msg" -> msg))))
	})
	
	def user_forget_password = Action(request => requestArgsQuery().requestArgsV2(request) {jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(msg_user_email_check(jv) :: msg_user_forget_password(jv) ::  msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "msg" -> msg))))
	})
	
	def user_token_op = Action(request => requestArgsQuery().requestArgsV2(request) {jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		
		MessageRoutes(MsgAuthTokenParser(jv) :: MsgAuthTokenExpire(jv) :: msg_user_token_op(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "msg" -> msg))))
	})
	
	// 单纯给忘记密码与第一次登入使用
	def user_token_chang_pwd = Action(request => requestArgsQuery().requestArgsV2(request) {jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("change pwd"))), jv)
			:: MsgAuthCheckTokenAction(jv)
			:: MsgAuthTokenParser(jv)
			:: MsgAuthTokenExpire(jv)
			:: msg_user_token_op(jv)
//			:: msg_register_token_create(jv) TODO: 通讯协议不一致导致message不能重用，稍后重构
			:: msg_user_chang_pwd(jv)
			:: msg_first_push_user(jv)
			:: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "msg" -> msg))))
	})
	//核对密码并修改
	def user_chang_new_pwd = Action(request => requestArgsQuery().requestArgsV2(request) {jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("change pwd"))), jv)
			:: MsgAuthCheckTokenAction(jv)
			:: MsgAuthTokenParser(jv)
			:: MsgAuthTokenExpire(jv)
			:: msg_user_token_op(jv)
			:: msg_user_check_pwd(jv)
			:: msg_user_chang_pwd(jv)
			:: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "msg" -> msg))))
	})
}
