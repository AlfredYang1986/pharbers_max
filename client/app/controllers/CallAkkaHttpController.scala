package controllers

import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.LogMessage.msg_log
import com.pharbers.aqll.pattern.MessageRoutes
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import controllers.common.requestArgsQuery.requestArgs
import module.CallAkkaHttpModuleMessage._
import play.api.libs.json.Json.toJson
import play.api.mvc.{Controller, _}

/**
  * Created by qianpeng on 2017/2/13.
  */
class CallAkkaHttpController extends Controller{

    import pattern.LogMessage.common_log
    import pattern.ResultMessage.common_result

    def callHttpCheckExcelAjaxCall = Action (request => requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("callHttpCheckExcelAjaxCall"))), jv, request) :: msg_CallCheckExcel(jv) :: msg_CommonResultMessage() :: Nil, None)
    })

    def callHttpRunModelAjaxCall = Action (request => requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("callHttpRunModelAjaxCall"))), jv, request) :: msg_CallRunModel(jv) :: msg_CommonResultMessage() :: Nil, None)
    })

    def callHttpFileExportAjaxCall = Action (request => requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("callHttpFileExportAjaxCall"))), jv, request) :: msg_CallFileExport(jv) :: msg_CommonResultMessage() :: Nil, None)
    })

    def callHttpCommitRunResultAjaxCall = Action (request => requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("callHttpCommitRunResultAjaxCall"))), jv, request) :: msg_CallCommitRunData(jv) :: msg_CommonResultMessage() :: Nil, None)
    })

    def callHttpCleaningDataAjaxCall = Action (request => requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("callHttpCleaningDataAjaxCall"))), jv, request) :: msg_CallCleaningData(jv) :: msg_CommonResultMessage() :: Nil, None)
    })

    def callHttpUploadBeforeAjaxCall = Action (request => requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("callHttpUploadBeforeAjaxCall"))), jv, request) :: msg_CallUploadBefore(jv) :: msg_CommonResultMessage() :: Nil, None)
    })
}
