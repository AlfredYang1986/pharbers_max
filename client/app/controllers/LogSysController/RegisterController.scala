package controllers.LogSysController

import javax.inject.Inject

import akka.actor.ActorSystem
import bmlogic.register.RegisterMessage._
import com.pharbers.aqll.dbmodule.db.DBTrait
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage
import com.pharbers.token.AuthTokenTrait
import controllers.common.requestArgsQuery
import play.api.mvc.{Action, Controller}

/**
  * Created by yym on 9/17/17.
  */
class RegisterController @Inject () (as_inject : ActorSystem, dbt : DBTrait, att : AuthTokenTrait) extends Controller {
    implicit val as = as_inject
    
    def pushAdminController = Action(request => requestArgsQuery().requestArgsV2(request) {jv =>
        import com.pharbers.bmpattern.LogMessage.common_log
        import com.pharbers.bmpattern.ResultMessage.common_result
        MessageRoutes( msg_pushAdminCommand(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })
    
    def pushRegisterWithoutCheck = Action(request => requestArgsQuery().requestArgsV2(request){jv =>
        import com.pharbers.bmpattern.LogMessage.common_log
        import com.pharbers.bmpattern.ResultMessage.common_result
        MessageRoutes(msg_pushRegisterWithoutCheckCommand(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" ->att))))
        
    })
    
    def queryRegisterWithID = Action(request => requestArgsQuery().requestArgsV2(request){jv =>
        import com.pharbers.bmpattern.LogMessage.common_log
        import com.pharbers.bmpattern.ResultMessage.common_result
        MessageRoutes(msg_queryRegisterWithIDCommand(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" ->att))))
        
    })
    
    def queryAllRegisters = Action(request => requestArgsQuery().requestArgsV2(request){jv =>
        import com.pharbers.bmpattern.LogMessage.common_log
        import com.pharbers.bmpattern.ResultMessage.common_result
        MessageRoutes(msg_queryAllRegistersCommand(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" ->att))))
        
    })
    
    def checkRegisterStatus = Action(request => requestArgsQuery().requestArgsV2(request){jv =>
        import com.pharbers.bmpattern.LogMessage.common_log
        import com.pharbers.bmpattern.ResultMessage.common_result
        MessageRoutes(msg_checkRegisterStatusCommand(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" ->att))))
        
    })
    
    def deleteRegister = Action(request => requestArgsQuery().requestArgsV2(request){jv =>
        import com.pharbers.bmpattern.LogMessage.common_log
        import com.pharbers.bmpattern.ResultMessage.common_result
        MessageRoutes(msg_deleteRegisterCommand(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" ->att))))
        
    })
    
    def cryptRegisterCode = Action(request => requestArgsQuery().requestArgsV2(request){jv =>
        import com.pharbers.bmpattern.LogMessage.common_log
        import com.pharbers.bmpattern.ResultMessage.common_result
        MessageRoutes(msg_cryptRegisterCodeCommand(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" ->att))))
        
    })
    
    def decryptRegisterCode = Action(request => requestArgsQuery().requestArgsV2(request){jv =>
        import com.pharbers.bmpattern.LogMessage.common_log
        import com.pharbers.bmpattern.ResultMessage.common_result
        MessageRoutes(msg_decryptRegisterCodeCommand(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" ->att))))
        
    })
    
    def checkAuthTokenExpire = Action(request => requestArgsQuery().requestArgsV2(request){jv =>
        import com.pharbers.bmpattern.LogMessage.common_log
        import com.pharbers.bmpattern.ResultMessage.common_result
        MessageRoutes(msg_checkAuthTokenExpireCommand(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" ->att))))
        
    })
    
}
